package ac.at.tuwien.sbc.broker.workflow;

import ac.at.tuwien.sbc.domain.configuration.MarketArgsConfiguration;
import ac.at.tuwien.sbc.domain.entry.*;
import ac.at.tuwien.sbc.domain.enums.OrderStatus;
import ac.at.tuwien.sbc.domain.enums.OrderType;
import ac.at.tuwien.sbc.domain.enums.ShareType;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import ac.at.tuwien.sbc.domain.exception.CoordinationServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 27/03/15.
 */
@Service
public class Workflow {
    
    /** The broker id. */
    @Value("${id}")
    private Integer brokerId;

    /** The coordination service. */
    @Autowired
    private ICoordinationService coordinationService;

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(Workflow.class);

    /** The Constant brokerProvision. */
    public static final Double brokerProvision = 0.03;

    /** The Constant brokerProvision. */
    public static final Double fondFee = 0.02;

    @Autowired
    private MarketArgsConfiguration marketArgs;


    /**
     * On post construct.
     */
    @PostConstruct
    private void onPostConstruct() {

        handleReleaseRequests();
        initOrderRequestHandling();
        initFirstOrderRequestHandling();
        initReleaseRequestHandling();
        initShareNotification();
    }

    /**
     * Inits the release request handling.
     */
    private void initReleaseRequestHandling() {
        coordinationService.registerReleaseNotification(new CoordinationListener<ArrayList<ReleaseEntry>>() {
            @Override
            public void onResult(ArrayList<ReleaseEntry> releaseEntries) {
                logger.info("on ReleaseEntry notification" );
                handleReleaseRequests();
            }
        });
    }

    /**
     * Inits the order request handling.
     */
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

    /**
     * Inits the first order request handling.
     */
    private void initFirstOrderRequestHandling() {
        ArrayList<ShareEntry> shareList = coordinationService.readShares();

        if (shareList != null && !shareList.isEmpty()) {
            for (ShareEntry se : shareList)
                handleOrderRequests(se.getShareID());
        }
    }

    /**
     * Inits the share notification.
     */
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

    /**
     * Handle release requests.
     */
    private void handleReleaseRequests() {

        Boolean tryAgain = true;
        while (tryAgain) {

            Object sharedTransaction = coordinationService.createTransaction(1000L, (String) marketArgs.getMarkets().get(0));
            ReleaseEntry releaseEntry = coordinationService.getReleaseEntry(sharedTransaction);

            if (releaseEntry != null) {

                //update or create share
                ShareEntry shareEntry = coordinationService.getShareEntry(releaseEntry.getCompanyID(), sharedTransaction);

                if (shareEntry == null) {
                    shareEntry = new ShareEntry(releaseEntry.getCompanyID(), releaseEntry.getNumShares(), releaseEntry.getPrice(), releaseEntry.getShareType());
                    logger.info("INIT SHARE: " + shareEntry.getShareID() + " / " + shareEntry.getNumShares() + "/" + shareEntry.getPrice());
                } else {
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
                        null,
                        releaseEntry.getCompanyID(),
                        OrderType.SELL,
                        0.0,
                        releaseEntry.getNumShares(),
                        0,
                        OrderStatus.OPEN,
                        false);

                try {
                    coordinationService.addOrder(oe, sharedTransaction, false);
                    coordinationService.commitTransaction(sharedTransaction);
                    logger.info("Broker " + brokerId + " , converted release into OrderEntry " + oe.getOrderID());
                } catch (CoordinationServiceException e) {
                    coordinationService.rollbackTransaction(sharedTransaction);
                }
            }
            else {
                tryAgain = false;
            }
        }
    }


    /**
     * Handle order requests.
     *
     * @param shareId the share id
     */
    private synchronized void handleOrderRequests(String shareId) {

        //create shared transaction
        Object sharedOrderRequestTransaction = coordinationService.createTransaction(1000L, (String) marketArgs.getMarkets().get(0));

        //get share entry
        ShareEntry shareEntry = coordinationService.getShareEntry(shareId, sharedOrderRequestTransaction);

        if (shareEntry == null) {
            logger.info("Share entry not found");
            coordinationService.rollbackTransaction(sharedOrderRequestTransaction);
            sharedOrderRequestTransaction = null;
            return;
        }


        OrderEntry sellOrder = getOrderEntryByShareEntry(shareEntry, OrderType.SELL, sharedOrderRequestTransaction);

        //return and rollback if no buy order exists
        if (sellOrder == null) {
            logger.info("No sell order available");
            coordinationService.rollbackTransaction(sharedOrderRequestTransaction);
            sharedOrderRequestTransaction = null;
            return;
        }

        //find corresponding buy order
        OrderEntry buyOrder = getOrderEntryByShareEntry(shareEntry, OrderType.BUY, sharedOrderRequestTransaction);

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
        DepotEntry seller = null;
        if (sellOrder.getInvestorID() != null) {
           seller = coordinationService.getDepot(sellOrder.getInvestorID(), sharedOrderRequestTransaction);
        }
        //get buyer
        DepotEntry buyer = coordinationService.getDepot(buyOrder.getInvestorID(), sharedOrderRequestTransaction);

        if (seller != null && buyer != null)
            logger.info("seller:" + seller.getId() + ", buyer:" + buyer.getId());

        if (buyer == null || (sellOrder.getInvestorID() != null && seller == null)) {

            if (sharedOrderRequestTransaction == null)
                doManualRollback(sellOrder, buyOrder, seller, buyer);

            coordinationService.rollbackTransaction(sharedOrderRequestTransaction);
            sharedOrderRequestTransaction = null;
            return;
        }

        //check if transaction is valid
        HashMap<OrderEntry, Boolean> validationResult =
                TransactionValidator.validate(sellOrder, buyOrder, seller, buyer, shareEntry, brokerProvision, fondFee);

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
                if (seller != null)
                    coordinationService.setDepot(seller, sharedOrderRequestTransaction, false);

                coordinationService.setDepot(buyer, sharedOrderRequestTransaction, false);
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

    /**
     * Do transaction.
     *
     * @param sellOrder the sell order
     * @param buyOrder the buy order
     * @param seller the seller
     * @param buyer the buyer
     * @param shareEntry the share entry
     * @param sharedTransaction the shared transaction
     * @throws CoordinationServiceException the coordination service exception
     */
    private void doTransaction(OrderEntry sellOrder, OrderEntry buyOrder, DepotEntry seller, DepotEntry buyer, ShareEntry shareEntry, Object sharedTransaction) throws CoordinationServiceException {

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


        double currentFondFee = shareEntry.getShareType().equals(ShareType.FOND) ? fondFee : 0.0;
        //skip if seller is a company
        Double brokerProvisionSeller = 0.0;

        if (seller != null) {

            brokerProvisionSeller = (sellOrder.getPrioritized() ? brokerProvision*2 : brokerProvision);
            //set new budget
            seller.setBudget(seller.getBudget() + (shareEntry.getPrice() * numSharesToTransact * (1 - brokerProvisionSeller - currentFondFee)));
            //decrease num shares
            seller.getShareDepot().put(shareEntry.getShareID(), seller.getShareDepot().get(shareEntry.getShareID()) - numSharesToTransact);
        }

        Double brokerProvisionBuyer = (buyOrder.getPrioritized() ? brokerProvision*2 : brokerProvision);
        buyer.setBudget(buyer.getBudget() - (shareEntry.getPrice() * numSharesToTransact * (1 + brokerProvisionBuyer + currentFondFee)));


        //add fee to fond budget
        if (shareEntry.getShareType().equals(ShareType.FOND)) {
            //get fond manager depot and add fee to budget
            DepotEntry fondDepot = coordinationService.getDepot(shareEntry.getShareID(), sharedTransaction);
            if (fondDepot != null) {
                Double sumFees = (seller != null) ? 2*currentFondFee : currentFondFee;
                fondDepot.setBudget(fondDepot.getBudget() + (shareEntry.getPrice() * numSharesToTransact) * sumFees);
                coordinationService.setDepot(fondDepot, sharedTransaction, false);
            }
        }

        Integer currentNumShare = buyer.getShareDepot().containsKey(shareEntry.getShareID()) ? buyer.getShareDepot().get(shareEntry.getShareID()) : 0;
        buyer.getShareDepot().put(shareEntry.getShareID(), currentNumShare + numSharesToTransact);

        //create transaction
        TransactionEntry transactionEntry = new TransactionEntry();
        transactionEntry.setTransactionID(UUID.randomUUID().toString());
        transactionEntry.setBrokerID(brokerId);
        transactionEntry.setSellerID(seller != null ? seller.getId() : null);
        transactionEntry.setBuyerID(buyer.getId());
        transactionEntry.setSellOrderID(sellOrder.getOrderID());
        transactionEntry.setBuyOrderID(buyOrder.getOrderID());
        transactionEntry.setShareID(shareEntry.getShareID());
        transactionEntry.setPrice(shareEntry.getPrice());
        transactionEntry.setNumShares(numSharesToTransact);
        transactionEntry.setSumPrice(numSharesToTransact * shareEntry.getPrice());
        transactionEntry.setProvision((shareEntry.getPrice() * numSharesToTransact * (brokerProvisionSeller+brokerProvisionBuyer)));


        //update investors
        if (seller != null)
            coordinationService.setDepot(seller, sharedTransaction, false);

        coordinationService.setDepot(buyer, sharedTransaction, false);
        //add transaction
        coordinationService.addTransaction(transactionEntry, sharedTransaction);

        coordinationService.commitTransaction(sharedTransaction);
        sharedTransaction = null;
        //update orders
        Object addOrdersTransaction = coordinationService.createTransaction(1000L, (String) marketArgs.getMarkets().get(0));
        coordinationService.addOrder(sellOrder, addOrdersTransaction, false);
        coordinationService.addOrder(buyOrder, addOrdersTransaction, false);
        coordinationService.commitTransaction(addOrdersTransaction);
    }

    /**
     * Do manual rollback.
     *
     * @param sellOrder the sell order
     * @param buyOrder the buy order
     * @param seller the seller
     * @param buyer the buyer
     */
    private void doManualRollback(OrderEntry sellOrder, OrderEntry buyOrder, DepotEntry seller, DepotEntry buyer) {

        try {
            if (sellOrder != null)
                coordinationService.addOrder(sellOrder, null, true);
            if (buyOrder != null)
                coordinationService.addOrder(buyOrder, null, true);
            if (seller != null)
                coordinationService.setDepot(seller, null, true);
            if (buyer != null)
                coordinationService.setDepot(buyer, null, true);
        } catch (CoordinationServiceException e) {e.printStackTrace();}
    }

    /**
     * Get order entry by share entry
     * @param shareEntry
     * @param orderType
     * @param sharedTransaction
     * @return
     */
    private OrderEntry getOrderEntryByShareEntry(ShareEntry shareEntry, OrderType orderType, Object sharedTransaction) {

        OrderEntry order = null;

        order = coordinationService.getOrderByProperties(shareEntry.getShareID(), orderType, OrderStatus.PARTIAL, true, shareEntry.getPrice(), sharedTransaction);
        //try with open sell orders
        if (order == null)
            order = coordinationService.getOrderByProperties(shareEntry.getShareID(), orderType, OrderStatus.OPEN, true, shareEntry.getPrice(), sharedTransaction);

        if (order == null)
            order = coordinationService.getOrderByProperties(shareEntry.getShareID(), orderType, OrderStatus.PARTIAL, false, shareEntry.getPrice(), sharedTransaction);

        if (order == null)
            order = coordinationService.getOrderByProperties(shareEntry.getShareID(), orderType, OrderStatus.OPEN, false, shareEntry.getPrice(), sharedTransaction);

        return order;
    }
}


