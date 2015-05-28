package ac.at.tuwien.sbc.investor.workflow;

import ac.at.tuwien.sbc.domain.entry.DepotEntry;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import ac.at.tuwien.sbc.domain.enums.OrderStatus;
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

    /** The coordination service. */
    @Autowired
    private ICoordinationService coordinationService;

    /** The observer. */
    @Autowired
    private IWorkFlowObserver observer;

    /** The current investor. */
    private DepotEntry currentInvestor;


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

                        currentInvestor = ide;
                        if (observer != null)
                            observer.onInvestorDepotEntryNotification(ide);

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
                    if (oe.getInvestorID().equals(depotId)) {
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

                if (currentInvestor == null)
                    return;

                for (ShareEntry s : sList) {
                    if (currentInvestor.getShareDepot().containsKey(s.getShareID())) {
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

        coordinationService.getInvestor(depotId, new CoordinationListener<DepotEntry>() {
            @Override
            public void onResult(DepotEntry ide) {
                if (ide == null) {
                    logger.info("Got InvestorDepotEntry is null");
                    //new entry
                    ide = new DepotEntry(depotId, budget, new HashMap<String, Integer>());
                }
                else {
                    logger.info("Got InvestorDepotEntry: " + ide.getId() + "/" + ide.getBudget());
                    //increase budget if already exists
                    ide.setBudget(ide.getBudget()+budget);
                }
                coordinationService.setInvestor(ide);
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
        if (currentInvestor == null)
            return;
        //get shares for investor
        ArrayList<String> shareIds = new ArrayList<String>();
        shareIds.addAll(currentInvestor.getShareDepot().keySet());

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
