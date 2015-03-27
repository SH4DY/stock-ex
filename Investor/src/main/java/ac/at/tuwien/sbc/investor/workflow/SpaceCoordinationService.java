package ac.at.tuwien.sbc.investor.workflow;

import ac.at.tuwien.sbc.domain.entry.InvestorDepotEntry;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import org.mozartspaces.capi3.KeyCoordinator;
import org.mozartspaces.capi3.LindaCoordinator;
import org.mozartspaces.core.*;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dietl_ma on 26/03/15.
 */
@Service
@Profile("space")
public class SpaceCoordinationService implements ICoordinationService {

    @Autowired
    MzsCore core;

    @Autowired
    Capi capi;

    @Autowired
    @Qualifier("investorDepotContainer")
    ContainerReference investorDepotContainer;

    @Autowired
    @Qualifier("orderContainer")
    ContainerReference orderContainer;

    private ICoordinationServiceListener listener;

    private ArrayList<Notification> notifications = new ArrayList<Notification>();

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(SpaceCoordinationService.class);

    @Override
    public void setListener(ICoordinationServiceListener l) {
        listener = l;
    }

    @Override
    public void getInvestor(Integer id, CoordinationListener cListener) {
        logger.info("Try to read investor with arguments: " + String.valueOf(id));

        ArrayList<InvestorDepotEntry> entries = null;
        try {
            entries = capi.read(investorDepotContainer, KeyCoordinator.newSelector(id.toString()), MzsConstants.RequestTimeout.ZERO, null);
        } catch (MzsCoreException e) {
            logger.info("Investor depot not found for: " + id);
        }

        if (entries != null && !entries.isEmpty())
            //listener.onGetInvestor(entries.get(0));
            cListener.onResult(entries.get(0));
        else {
            cListener.onResult(null);
        }

    }

    @Override
    public void registerInvestorNotification(Integer id, CoordinationListener clistener) {

        try {
            NotificationManager notificationManager = new NotificationManager(core);

            Notification notification = notificationManager.createNotification(investorDepotContainer,
                    new InvestorDepotNotificationListener(clistener, id),
                    Operation.WRITE);

            notifications.add(notification);

        } catch (MzsCoreException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerOrderNotification(Integer id, CoordinationListener clistener) {
        try {
            NotificationManager notificationManager = new NotificationManager(core);

            Notification notification = notificationManager.createNotification(orderContainer,
                    new OrderNotificationListener(clistener, id),
                    Operation.WRITE);

            notifications.add(notification);

        } catch (MzsCoreException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setInvestor(InvestorDepotEntry ide) {

        logger.info("Try to write InvestorDepotEntry: " + ide.getBudget().toString());
        TransactionReference tx = null;
        try {
            tx = capi.createTransaction(1000, investorDepotContainer.getSpace());

            try {
                capi.take(investorDepotContainer, KeyCoordinator.newSelector(ide.getInvestorID().toString()), MzsConstants.RequestTimeout.TRY_ONCE, tx);
            }
            catch (MzsCoreException e) {}

            Entry entryToUpdate = new Entry(ide, KeyCoordinator.newCoordinationData(ide.getInvestorID().toString()));
            capi.write(investorDepotContainer, MzsConstants.RequestTimeout.ZERO, tx, entryToUpdate);

            capi.commitTransaction(tx);
        }
        catch (MzsCoreException e) {
            try {
                capi.rollbackTransaction(tx);
            } catch (MzsCoreException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            logger.info("Something went wrong writing a InvestorDepotEntry");
        }
    }

    @Override
    public void addOrder(OrderEntry oe) {
        try {
            capi.write(orderContainer, new Entry(oe));
        } catch (MzsCoreException e) {
            logger.info("Something went wrong writing an order");
        }
    }

    @Override
    public void getOrders(Integer investorId, CoordinationListener cListener) {

        logger.info("Try to read orders by template");
        OrderEntry template = new OrderEntry(null, investorId, null, null, null, null, null, null);
        ArrayList<OrderEntry> entries = null;
        try {
            entries = capi.read(orderContainer, LindaCoordinator.newSelector(template, MzsConstants.Selecting.COUNT_ALL), MzsConstants.RequestTimeout.DEFAULT, null);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        if (entries != null && !entries.isEmpty())
            //listener.onGetInvestor(entries.get(0));
            cListener.onResult(entries);
        else {
            cListener.onResult(null);
        }
    }

    @PreDestroy
    public void onPreDestroy() {

        //destroy notifications
        for (Notification n : notifications) {
            try {
                n.destroy();
            } catch (MzsCoreException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * InvestorDepotNotificationListener
     */
    public class InvestorDepotNotificationListener implements NotificationListener {

        CoordinationListener<InvestorDepotEntry> callbackListener;
        Integer investorID;

        public InvestorDepotNotificationListener(CoordinationListener<InvestorDepotEntry> callbackListener, Integer investorID) {
            this.callbackListener = callbackListener;
            this.investorID = investorID;
        }

        @Override
        public void entryOperationFinished(Notification notification, Operation operation, List<? extends Serializable> entries) {

            for (Serializable entry : entries) {
                InvestorDepotEntry ide = ((InvestorDepotEntry)((Entry)entry).getValue());
                if (ide.getInvestorID().equals(investorID)) {
                    callbackListener.onResult(ide);
                    break;
                }
            }

        }
    }

    /**
     * InvestorDepotNotificationListener
     */
    public class OrderNotificationListener implements NotificationListener {

        CoordinationListener<OrderEntry> callbackListener;
        Integer investorID;

        public OrderNotificationListener(CoordinationListener<OrderEntry> callbackListener, Integer investorID) {
            this.callbackListener = callbackListener;
            this.investorID = investorID;
        }

        @Override
        public void entryOperationFinished(Notification notification, Operation operation, List<? extends Serializable> entries) {

            for (Serializable entry : entries) {
                OrderEntry oe = ((OrderEntry)((Entry)entry).getValue());
                if (oe.getInvestorID().equals(investorID)) {
                    callbackListener.onResult(oe);
                }
            }

        }
    }
}
