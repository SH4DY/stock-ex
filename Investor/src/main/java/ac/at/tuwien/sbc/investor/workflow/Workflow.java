package ac.at.tuwien.sbc.investor.workflow;

import ac.at.tuwien.sbc.domain.configuration.MarketArgsConfiguration;
import ac.at.tuwien.sbc.domain.entry.DepotEntry;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ReleaseEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import ac.at.tuwien.sbc.domain.enums.DepotType;
import ac.at.tuwien.sbc.domain.enums.OrderStatus;
import ac.at.tuwien.sbc.domain.enums.ShareType;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import ac.at.tuwien.sbc.investor.configuration.MarketArgsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 26/03/15.
 */
@Service
public class Workflow  {

    /** The investor depotId. */
    @Value("${id}")
    private String depotId;

    @Value("${type:INVESTOR}")
    private DepotType depotType;


    /** The coordination service. */
    @Autowired
    private ICoordinationService coordinationService;

    /** The observer. */
    @Autowired
    private IWorkFlowObserver observer;

    @Autowired
    private MarketArgsConfiguration<MarketArgsMapper.MarketArgs> marketArgs;

    /** The current investor. */
    private HashMap<String, DepotEntry> currentDepot;

    private Boolean releaseDone = false;

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(Workflow.class);

    /**
     * On post construct.
     */
    @PostConstruct
    private void onPostConstruct() {

        currentDepot = new HashMap<>();
        //init notifications
        initDepotNotification();
        initOrderNotification();
        initShareNotification();
        //init investor
        initDepot();
        //init orders
        initOrders();
        //init shares
        initShares();

    }



    /**
     * Inits the investor notification.
     */
    private void initDepotNotification() {
        for (final String market : marketArgs.getMarkets()) {
            coordinationService.registerDepotNotification(new CoordinationListener<ArrayList<DepotEntry>>() {
                @Override
                public void onResult(ArrayList<DepotEntry> ideList) {
                    for (DepotEntry de : ideList) {
                        if (de.getId().equals(depotId)) {

                            currentDepot.put(market, de);
                            if (observer != null)
                                observer.onDepotEntryNotification(de, market);

                            //re-init shares
                            initShares();
                        }
                    }
                }
            }, market);
        }
    }

    /**
     * Inits the order notification.
     */
    private void initOrderNotification() {
        for (String market : marketArgs.getMarkets()) {
            coordinationService.registerOrderNotification(new CoordinationListener<ArrayList<OrderEntry>>() {
                @Override
                public void onResult(ArrayList<OrderEntry> oeList) {
                    for (OrderEntry oe : oeList) {
                        if (oe.getInvestorID() != null && oe.getInvestorID().equals(depotId)) {
                            if (observer != null)
                                observer.onOrderEntryNotification(oe);
                        }
                    }
                }
            }, market);
        }
    }

    /**
     * Inits the share notification.
     */
    private void initShareNotification() {
        for (String market : marketArgs.getMarkets()) {
            coordinationService.registerShareNotification(new CoordinationListener<ArrayList<ShareEntry>>() {
                @Override
                public void onResult(ArrayList<ShareEntry> sList) {


                    if (currentDepot.isEmpty())
                        return;

                    HashMap<String, Integer> shares = new HashMap<String, Integer>();
                    for (String market : marketArgs.getMarkets()) {
                        shares.putAll(currentDepot.get(market).getShareDepot());
                    }

                    for (ShareEntry s : sList) {
                        if (shares.containsKey(s.getShareID())) {
                            if (observer != null)
                                observer.onShareEntryNotification(s);
                        }
                    }
                }
            }, market);
        }
    }
    
    /**
     * Get investor if exists and increase budget by new args.
     */
    private void initDepot() {

        for (final String market : marketArgs.getMarkets()) {

            coordinationService.getDepot(new CoordinationListener<DepotEntry>() {
                @Override
                public void onResult(DepotEntry de) {

                    if (de == null) {
                        logger.info("Got DepotEntry is null");
                        //new entry
                        de = new DepotEntry(depotId, marketArgs.getArgsByMarket(market).getBudget(), depotType, new HashMap<String, Integer>());
                        //handle release request once if fond manager is new
                        if (de.getDepotType().equals(DepotType.FOND_MANAGER))
                            initReleaseRequest(de);


                    } else {
                        logger.info("Got DepotEntry: " + de.getId() + "/" + de.getBudget());

                        //increase budget if already exists and is a investor
                        if (de.getDepotType().equals(DepotType.INVESTOR))
                            de.setBudget(de.getBudget() + marketArgs.getArgsByMarket(market).getBudget());
                    }
                    coordinationService.setDepot(de, market);

                }
            }, market, depotId);
        }
    }

    /**
     * Inits the orders.
     */
    private void initOrders() {
        for (String market : marketArgs.getMarkets()) {
            coordinationService.getOrders(depotId, market , new CoordinationListener<ArrayList<OrderEntry>>() {
                @Override
                public void onResult(ArrayList<OrderEntry> entries) {

                    if (entries != null) {
                        for (OrderEntry oe : entries) {
                            if (observer != null)
                                observer.onOrderEntryNotification(oe);
                        }
                    }
                }
            });
        }
    }

    /**
     * Inits the shares.
     */
    private void initShares() {
        if (currentDepot.isEmpty())
            return;

        //get shares for depot

        for (String market : marketArgs.getMarkets()) {
            ArrayList<String> shareIds = new ArrayList<String>();

            if (!currentDepot.containsKey(market))
                continue;

            shareIds.addAll(currentDepot.get(market).getShareDepot().keySet());

            coordinationService.getShares(shareIds, market , new CoordinationListener<ArrayList<ShareEntry>>() {
                @Override
                public void onResult(ArrayList<ShareEntry> seList) {
                    for (ShareEntry s : seList) {
                        if (observer != null)
                            observer.onShareEntryNotification(s);

                    }
                }
            });
        }
    }

    private void initReleaseRequest(DepotEntry de) {

        if (releaseDone)
            return;

        for (String market : marketArgs.getMarkets()) {

            if (marketArgs.getArgsByMarket(market).getNumShares() != null) {
                Double initPrice = marketArgs.getArgsByMarket(market).getBudget() / marketArgs.getArgsByMarket(market).getNumShares();
                ReleaseEntry releaseEntry = new ReleaseEntry();
                releaseEntry.setCompanyID(de.getId());
                releaseEntry.setNumShares(marketArgs.getArgsByMarket(market).getNumShares());
                releaseEntry.setPrice(initPrice);
                releaseEntry.setShareType(ShareType.FOND);

                //only make releases on the first given market
                coordinationService.makeRelease(releaseEntry, market);
                releaseDone = true;
                break;
            }
        }
    }
    
    /**
     * Adds the order.
     *
     * @param oe the oe
     * @param market
     */
    public void addOrder(OrderEntry oe, String market) {

        oe.setOrderID(UUID.randomUUID());
        oe.setInvestorID(depotId);
        oe.setStatus(OrderStatus.OPEN);
        oe.setNumCompleted(0);

        coordinationService.addOrder(oe, market);
    }

    /**
     * Delete order.
     *  @param orderID the order depotId
     *
     */
    public void deleteOrder(UUID orderID) {

        for (String market : marketArgs.getMarkets()) {
            coordinationService.deleteOrder(orderID, market);
        }
    }
}
