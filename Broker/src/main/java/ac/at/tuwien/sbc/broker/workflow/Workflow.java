package ac.at.tuwien.sbc.broker.workflow;

import ac.at.tuwien.sbc.domain.entry.*;
import ac.at.tuwien.sbc.domain.enums.OrderStatus;
import ac.at.tuwien.sbc.domain.enums.OrderType;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import ac.at.tuwien.sbc.domain.exception.CoordinationServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by dietl_ma on 27/03/15.
 */
@Service
public class Workflow {
    @Value("${id}")
    private Integer brokerId;

    @Autowired
    private ICoordinationService coordinationService;

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(Workflow.class);

    public static final Double brokerProvision = 0.03;

    @PostConstruct
    private void onPostConstruct() {

        handleReleaseRequests();
        initOrderRequestHandling();
        initFirstOrderRequestHandling();
        initReleaseRequestHandling();
        initShareNotification();
    }

    private void initReleaseRequestHandling() {
        coordinationService.registerReleaseNotification(new CoordinationListener<ArrayList<ReleaseEntry>>() {
            @Override
            public void onResult(ArrayList<ReleaseEntry> releaseEntries) {
                logger.info("on ReleaseEntry notification" );
                handleReleaseRequests();
            }
        });
    }

    private void initOrderRequestHandling() {
        coordinationService.registerOrderNotification(new CoordinationListener<ArrayList<OrderEntry>>() {
            @Override
            public void onResult(ArrayList<OrderEntry> oeList) {
                logger.info("on OrderEntry notification" );
                ArrayList<String> shareIds= new ArrayList<String>();
                for (OrderEntry oe : oeList) {
                   handleOrderRequests(oe.getShareID());
                }
            }
        });
    }

    private void initFirstOrderRequestHandling() {
        ArrayList<ShareEntry> shareList = coordinationService.readShares();

        if (shareList != null && !shareList.isEmpty()) {
            for (ShareEntry se : shareList)
                handleOrderRequests(se.getShareID());
        }
    }

    private void initShareNotification() {
        coordinationService.registerShareNotification(new CoordinationListener<ArrayList<ShareEntry>>() {
            @Override
            public void onResult(ArrayList<ShareEntry> entries) {
                if (entries != null && !entries.isEmpty()) {
                    for (ShareEntry se : entries)
                        handleOrderRequests(se.getShareID());
                }
            }
        });
    }

    private void handleReleaseRequests() {
        Object sharedTransaction = coordinationService.createTransaction(1000L);
        ReleaseEntry releaseEntry = coordinationService.getReleaseEntry(sharedTransaction);

        if (releaseEntry == null) {
            return;
        }
        //update or create share
        ShareEntry shareEntry = coordinationService.getShareEntry(releaseEntry.getCompanyID(), sharedTransaction);

        if (shareEntry == null) {
            shareEntry = new ShareEntry(releaseEntry.getCompanyID(), releaseEntry.getNumShares(), releaseEntry.getPrice());
            logger.info("INIT SHARE: " + shareEntry.getShareID() + " / " + shareEntry.getNumShares() + "/" + shareEntry.getPrice());
        }
        else {
            shareEntry.setNumShares(shareEntry.getNumShares() + releaseEntry.getNumShares());
            logger.info("SET SHARE: " + shareEntry.getShareID() + " / " + shareEntry.getNumShares() + "/" + shareEntry.getPrice());
        }

        try {
            coordinationService.setShareEntry(shareEntry, sharedTransaction);
        } catch (CoordinationServiceException e) {
            coordinationService.rollbackTransaction(sharedTransaction);
            return;
        }

        OrderEntry oe = new OrderEntry(UUID.randomUUID(),
                0,
                releaseEntry.getCompanyID(),
                OrderType.SELL,
                0.0,
                releaseEntry.getNumShares(),
                0,
                OrderStatus.OPEN);

        try {
            coordinationService.addOrder(oe, sharedTransaction, false);
            coordinationService.commitTransaction(sharedTransaction);
            logger.info("Broker " + brokerId +" , converted release into OrderEntry " + oe.getOrderID());
        } catch (CoordinationServiceException e) {
            coordinationService.rollbackTransaction(sharedTransaction);
        }
    }


    private synchronized void handleOrderRequests(String shareId) {

        //create shared transaction
        Object sharedOrderRequestTransaction = coordinationService.createTransaction(1000L);


        //get share entry
        ShareEntry shareEntry = coordinationService.getShareEntry(shareId, sharedOrderRequestTransaction);

        if (shareEntry == null) {
            logger.info("Share entry not found");
            coordinationService.rollbackTransaction(sharedOrderRequestTransaction);
            sharedOrderRequestTransaction = null;
            return;
        }

        //take partial sell orders first
        OrderEntry sellOrder = null;

        sellOrder = coordinationService.getOrderByProperties(shareId, OrderType.SELL, OrderStatus.PARTIAL, shareEntry.getPrice(), sharedOrderRequestTransaction);
        //try with open sell orders
        if (sellOrder == null)
            sellOrder = coordinationService.getOrderByProperties(shareId, OrderType.SELL, OrderStatus.OPEN, shareEntry.getPrice(), sharedOrderRequestTransaction);


        //return and rollback if no buy order exists
        if (sellOrder == null) {
            logger.info("No sell order available");
            coordinationService.rollbackTransaction(sharedOrderRequestTransaction);
            sharedOrderRequestTransaction = null;
            return;
        }

        //find corresponding buy order
        OrderEntry buyOrder = null;

        buyOrder = coordinationService.getOrderByProperties(shareId, OrderType.BUY, OrderStatus.PARTIAL, shareEntry.getPrice(), sharedOrderRequestTransaction);
        //try with opm sell order
        if (buyOrder == null) {
            buyOrder = coordinationService.getOrderByProperties(shareId, OrderType.BUY, OrderStatus.OPEN, shareEntry.getPrice(), sharedOrderRequestTransaction);
        }

        //return and rollback if no corresponding sell order exists
        if (buyOrder == null) {
            logger.info("No buy order available");

            if (sharedOrderRequestTransaction == null)
                doManualRollback(sellOrder, null, null, null);

            coordinationService.rollbackTransaction(sharedOrderRequestTransaction);
            sharedOrderRequestTransaction = null;
            return;
        }
        logger.info("Buy order found: " + buyOrder.getOrderID().toString());
        //check limits
        if (sellOrder.getLimit() > shareEntry.getPrice() ||
                buyOrder.getLimit() < shareEntry.getPrice()) {
            logger.info("Order limits not valid");

            if (sharedOrderRequestTransaction == null)
                doManualRollback(sellOrder, buyOrder, null, null);

            coordinationService.rollbackTransaction(sharedOrderRequestTransaction);
            sharedOrderRequestTransaction = null;
            return;
        }

        //get seller if seller is not a company
        InvestorDepotEntry seller = null;
        if (sellOrder.getInvestorID() != 0) {
           seller = coordinationService.getInvestor(sellOrder.getInvestorID(), sharedOrderRequestTransaction);
        }
        //get buyer
        InvestorDepotEntry buyer = coordinationService.getInvestor(buyOrder.getInvestorID(), sharedOrderRequestTransaction);

        if (seller != null && buyer != null)
            logger.info("seller:" + seller.getInvestorID() + ", buyer:" + buyer.getInvestorID());

        if (buyer == null || (sellOrder.getInvestorID() != 0 && seller == null)) {

            if (sharedOrderRequestTransaction == null)
                doManualRollback(sellOrder, buyOrder, seller, buyer);

            coordinationService.rollbackTransaction(sharedOrderRequestTransaction);
            sharedOrderRequestTransaction = null;
            return;
        }

        //check if transaction is valid
        HashMap<OrderEntry, Boolean> validationResult =
                TransactionValidator.validate(sellOrder, buyOrder, seller, buyer, shareEntry, brokerProvision);

        if (!validationResult.get(sellOrder) || !validationResult.get(buyOrder)) {
            logger.info("Transaction is invalid");

            if (!validationResult.get(sellOrder)) {
                sellOrder.setStatus(OrderStatus.DELETED);
            }
            if (!validationResult.get(buyOrder)) {
                buyOrder.setStatus(OrderStatus.DELETED);
            }

            try {
                //write updated order
                coordinationService.addOrder(sellOrder, sharedOrderRequestTransaction, false);
                coordinationService.addOrder(buyOrder, sharedOrderRequestTransaction, false);
                //write buyer and seller back
                coordinationService.setInvestor(seller, sharedOrderRequestTransaction, false);
                coordinationService.setInvestor(buyer, sharedOrderRequestTransaction, false);
            } catch (CoordinationServiceException e) {

                if (sharedOrderRequestTransaction == null)
                    doManualRollback(sellOrder, buyOrder, seller, buyer);

                coordinationService.rollbackTransaction(sharedOrderRequestTransaction);
                sharedOrderRequestTransaction = null;
            }
            coordinationService.commitTransaction(sharedOrderRequestTransaction);
            return;
        }
        else {
            //transaction seems to be valid
            logger.info("Transaction is valid");
            try {
                doTransaction(sellOrder, buyOrder, seller, buyer, shareEntry, sharedOrderRequestTransaction);
            } catch (CoordinationServiceException e1) {
                if (sharedOrderRequestTransaction == null)
                    doManualRollback(sellOrder, buyOrder, seller, buyer);

                coordinationService.rollbackTransaction(sharedOrderRequestTransaction);
                sharedOrderRequestTransaction = null;
            }
        }
    }

    private void doTransaction(OrderEntry sellOrder, OrderEntry buyOrder, InvestorDepotEntry seller, InvestorDepotEntry buyer, ShareEntry shareEntry, Object sharedTransaction) throws CoordinationServiceException {

        Integer numSharesToTransact = Math.min(sellOrder.getNumTotal() - sellOrder.getNumCompleted(),
                buyOrder.getNumTotal() - buyOrder.getNumCompleted());
        sellOrder.setNumCompleted(sellOrder.getNumCompleted() + numSharesToTransact);
        buyOrder.setNumCompleted(buyOrder.getNumCompleted() + numSharesToTransact);

        sellOrder.setStatus(OrderStatus.PARTIAL);
        if (sellOrder.getNumCompleted() >= sellOrder.getNumTotal())
            sellOrder.setStatus(OrderStatus.COMPLETED);

        buyOrder.setStatus(OrderStatus.PARTIAL);
        if (buyOrder.getNumCompleted() >= buyOrder.getNumTotal())
            buyOrder.setStatus(OrderStatus.COMPLETED);

        //skip if seller is a company
        if (seller != null) {
            //set new budget
            seller.setBudget(seller.getBudget() + (shareEntry.getPrice() * numSharesToTransact * (1 - brokerProvision)));
            //decrease num shares
            seller.getShareDepot().put(shareEntry.getShareID(), seller.getShareDepot().get(shareEntry.getShareID()) - numSharesToTransact);
        }

        buyer.setBudget(buyer.getBudget() - (shareEntry.getPrice() * numSharesToTransact * (1 + brokerProvision)));

        Integer currentNumShare = buyer.getShareDepot().containsKey(shareEntry.getShareID()) ? buyer.getShareDepot().get(shareEntry.getShareID()) : 0;
        buyer.getShareDepot().put(shareEntry.getShareID(), currentNumShare + numSharesToTransact);

        //create transaction
        TransactionEntry transactionEntry = new TransactionEntry();
        transactionEntry.setTransactionID(UUID.randomUUID().toString());
        transactionEntry.setBrokerID(brokerId);
        transactionEntry.setSellerID(seller != null ? seller.getInvestorID() : 0);
        transactionEntry.setBuyerID(buyer.getInvestorID());
        transactionEntry.setSellOrderID(sellOrder.getOrderID());
        transactionEntry.setBuyOrderID(buyOrder.getOrderID());
        transactionEntry.setShareID(shareEntry.getShareID());
        transactionEntry.setPrice(shareEntry.getPrice());
        transactionEntry.setNumShares(numSharesToTransact);
        transactionEntry.setSumPrice(numSharesToTransact * shareEntry.getPrice());
        transactionEntry.setProvision((shareEntry.getPrice() * numSharesToTransact * 2 * brokerProvision));


        //update investors
        if (seller != null)
            coordinationService.setInvestor(seller, sharedTransaction, false);

        coordinationService.setInvestor(buyer, sharedTransaction, false);
        //add transaction
        coordinationService.addTransaction(transactionEntry, sharedTransaction);

        coordinationService.commitTransaction(sharedTransaction);
        sharedTransaction = null;
        //update orders
        Object addOrdersTransaction = coordinationService.createTransaction(1000L);
        coordinationService.addOrder(sellOrder, addOrdersTransaction, false);
        coordinationService.addOrder(buyOrder, addOrdersTransaction, false);
        coordinationService.commitTransaction(addOrdersTransaction);
    }

    private void doManualRollback(OrderEntry sellOrder, OrderEntry buyOrder, InvestorDepotEntry seller, InvestorDepotEntry buyer) {

        try {
            if (sellOrder != null)
                coordinationService.addOrder(sellOrder, null, true);
            if (buyOrder != null)
                coordinationService.addOrder(buyOrder, null, true);
            if (seller != null)
                coordinationService.setInvestor(seller, null, true);
            if (buyer != null)
                coordinationService.setInvestor(buyer, null, true);
        } catch (CoordinationServiceException e) {e.printStackTrace();}
    }
}


