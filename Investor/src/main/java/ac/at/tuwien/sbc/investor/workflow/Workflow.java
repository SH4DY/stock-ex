package ac.at.tuwien.sbc.investor.workflow;

import ac.at.tuwien.sbc.domain.entry.DepotEntry;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ReleaseEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import ac.at.tuwien.sbc.domain.enums.DepotType;
import ac.at.tuwien.sbc.domain.enums.OrderStatus;
import ac.at.tuwien.sbc.domain.enums.ShareType;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
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
 * Created by dietl_ma on 26/03/15.
 */
@Service
public class Workflow  {

    /** The investor depotId. */
    @Value("${id}")
    private String depotId;

    /** The budget. */
    @Value("${budget}")
    private Double budget;

    @Value("${type:INVESTOR}")
    private DepotType depotType;

    @Value("${numShares:0}")
    private Integer numShares;
    /** The coordination service. */
    @Autowired
    private ICoordinationService coordinationService;

    /** The observer. */
    @Autowired
    private IWorkFlowObserver observer;

    /** The current investor. */
    private DepotEntry currentDepot;


    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(Workflow.class);

    /**
     * On post construct.
     */
    @PostConstruct
    private void onPostConstruct() {

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
        coordinationService.registerDepotNotification(new CoordinationListener<ArrayList<DepotEntry>>() {
            @Override
            public void onResult(ArrayList<DepotEntry> ideList) {
                for (DepotEntry ide : ideList) {
                    if (ide.getId().equals(depotId)) {

                        currentDepot = ide;
                        if (observer != null)
                            observer.onDepotEntryNotification(ide);

                        //re-init shares
                        initShares();
                    }
                }
            }
        });
    }

    /**
     * Inits the order notification.
     */
    private void initOrderNotification() {
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
        });
    }

    /**
     * Inits the share notification.
     */
    public void initShareNotification() {
        coordinationService.registerShareNotification(new CoordinationListener<ArrayList<ShareEntry>>() {
            @Override
            public void onResult(ArrayList<ShareEntry> sList) {

                if (currentDepot == null)
                    return;

                for (ShareEntry s : sList) {
                    if (currentDepot.getShareDepot().containsKey(s.getShareID())) {
                        if (observer != null)
                            observer.onShareEntryNotification(s);
                    }
                }
            }
        });
    }
    
    /**
     * Get investor if exists and increase budget by new args.
     */
    private void initDepot() {

        coordinationService.getDepot(depotId, new CoordinationListener<DepotEntry>() {
            @Override
            public void onResult(DepotEntry de) {
                if (de == null) {
                    logger.info("Got DepotEntry is null");
                    //new entry
                    de = new DepotEntry(depotId, budget, depotType, new HashMap<String, Integer>());
                    //handle release request once if fond manager is new
                    if (de.getDepotType().equals(DepotType.FOND_MANAGER))
                        initReleaseRequest(de);


                } else {
                    logger.info("Got DepotEntry: " + de.getId() + "/" + de.getBudget());

                    //increase budget if already exists and is a investor
                    if (de.getDepotType().equals(DepotType.INVESTOR))
                        de.setBudget(de.getBudget() + budget);
                }
                coordinationService.setDepot(de);
            }
        });
    }

    /**
     * Inits the orders.
     */
    private void initOrders() {
        coordinationService.getOrders(depotId, new CoordinationListener<ArrayList<OrderEntry>>() {
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

    /**
     * Inits the shares.
     */
    private void initShares() {
        if (currentDepot == null)
            return;
        //get shares for depot
        ArrayList<String> shareIds = new ArrayList<String>();
        shareIds.addAll(currentDepot.getShareDepot().keySet());

        coordinationService.getShares(shareIds, new CoordinationListener<ArrayList<ShareEntry>>() {
            @Override
            public void onResult(ArrayList<ShareEntry> seList) {
                for (ShareEntry s : seList) {
                    if (observer != null)
                        observer.onShareEntryNotification(s);

                }
            }
        });
    }

    private void initReleaseRequest(DepotEntry de) {

        ReleaseEntry releaseEntry = new ReleaseEntry();
        releaseEntry.setCompanyID(de.getId());
        releaseEntry.setNumShares(numShares);
        releaseEntry.setPrice(budget/numShares);
        releaseEntry.setShareType(ShareType.FOND);

        coordinationService.makeRelease(releaseEntry);
    }
    
    /**
     * Adds the order.
     *
     * @param oe the oe
     */
    public void addOrder(OrderEntry oe) {

        oe.setOrderID(UUID.randomUUID());
        oe.setInvestorID(depotId);
        oe.setStatus(OrderStatus.OPEN);
        oe.setNumCompleted(0);

        coordinationService.addOrder(oe);
    }

    /**
     * Delete order.
     *
     * @param orderID the order depotId
     */
    public void deleteOrder(UUID orderID) {
        coordinationService.deleteOrder(orderID);
    }
}
