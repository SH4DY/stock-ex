package ac.at.tuwien.sbc.investor.workflow;

import ac.at.tuwien.sbc.domain.entry.InvestorDepotEntry;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.enums.OrderStatus;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import ac.at.tuwien.sbc.investor.gui.MainGUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by dietl_ma on 26/03/15.
 */
@Service
public class Workflow implements ICoordinationServiceListener {

    @Value("${id}")
    private Integer investorId;

    @Value("${budget}")
    private Double budget;

    @Autowired
    private ICoordinationService coordinationService;

    @Autowired
    private IWorkFlowObserver observer;


    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(SpaceCoordinationService.class);

    @PostConstruct
    private void onPostConstruct() {

        //add listener to coordination service
        coordinationService.setListener(this);
        //init notifications
        initInvestorNotification();
        initOrderNotification();
        //init investor
        initInvestor();
        //init orders
        initOrders();
    }


    private void initInvestorNotification() {
        coordinationService.registerInvestorNotification(investorId, new CoordinationListener<InvestorDepotEntry>() {
            @Override
            public void onResult(InvestorDepotEntry ide) {
                logger.info("Got InvestorDepotEntry notification: " + ide.getInvestorID() + "/" + ide.getBudget());

                if (observer != null) {
                    logger.info("Observer not null");
                    observer.onInvestorDepotEntryNotification(ide);
                }
            }
        });
    }

    private void initOrderNotification() {
        coordinationService.registerOrderNotification(investorId, new CoordinationListener<OrderEntry>() {
            @Override
            public void onResult(OrderEntry oe) {
                logger.info("Got Order notification: " + oe.getInvestorID() + "/" + oe.getShareID());

                if (observer != null) {
                    observer.onOrderEntryNotification(oe);
                }
            }
        });
    }
    /**
     * Get investor if exists and increase budget by new args
     */
    private void initInvestor() {

        coordinationService.getInvestor(investorId, new CoordinationListener<InvestorDepotEntry>() {
            @Override
            public void onResult(InvestorDepotEntry ide) {
                if (ide == null) {
                    //new entry
                    ide = new InvestorDepotEntry(investorId, budget, new HashMap<String, Integer>());
                }
                else {
                    logger.info("Got InvestorDepotEntry: " + ide.getInvestorID() + "/" + ide.getBudget());
                    //increase budget if already exists
                    ide.setBudget(ide.getBudget()+budget);
                }
                coordinationService.setInvestor(ide);
            }
        });
    }

    private void initOrders() {
        coordinationService.getOrders(investorId, new CoordinationListener<ArrayList<OrderEntry>>() {
            @Override
            public void onResult(ArrayList<OrderEntry> entries) {

                if (entries != null) {
                    for (OrderEntry oe : entries) {
                        if (observer != null) {
                            observer.onOrderEntryNotification(oe);
                        }
                    }
                }
            }
        });
    }

    public void addOrder(OrderEntry oe) {

        oe.setOrderID(UUID.randomUUID());
        oe.setInvestorID(investorId);
        oe.setStatus(OrderStatus.OPEN);
        oe.setNumCompleted(0);

        coordinationService.addOrder(oe);
    }
}
