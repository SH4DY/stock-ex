package ac.at.tuwien.sbc.broker.workflow.space;

import ac.at.tuwien.sbc.broker.workflow.ICoordinationService;
import ac.at.tuwien.sbc.domain.configuration.CommonSpaceConfiguration;
import ac.at.tuwien.sbc.domain.entry.*;
import ac.at.tuwien.sbc.domain.enums.OrderStatus;
import ac.at.tuwien.sbc.domain.enums.OrderType;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import ac.at.tuwien.sbc.domain.exception.CoordinationServiceException;
import org.mozartspaces.capi3.*;
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
import static org.mozartspaces.capi3.Matchmakers.*;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dietl_ma on 28/03/15.
 */
@Service
@Profile("space")
public class SpaceCoordinationService implements ICoordinationService {

    @Autowired
    MzsCore core;

    @Autowired
    Capi capi;

    @Autowired
    @Qualifier("releaseContainer")
    ContainerReference releaseContainer;

    @Autowired
    @Qualifier("shareContainer")
    ContainerReference shareContainer;

    @Autowired
    @Qualifier("orderContainer")
    ContainerReference orderContainer;

    @Autowired
    @Qualifier("investorDepotContainer")
    ContainerReference investorDepotContainer;

    @Autowired
    @Qualifier("transactionContainer")
    ContainerReference transactionContainer;

    private ArrayList<Notification> notifications = new ArrayList<Notification>();



    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(SpaceCoordinationService.class);


    @Override
    public InvestorDepotEntry getInvestor(Integer investorId, Object sharedTransaction) {
        logger.info("Try to read investor with arguments: " + String.valueOf(investorId));
        TransactionReference tx = (TransactionReference)sharedTransaction;
        ArrayList<InvestorDepotEntry> entries = null;
        InvestorDepotEntry entry = null;
        try {
            entries = capi.take(investorDepotContainer, KeyCoordinator.newSelector(investorId.toString()), MzsConstants.RequestTimeout.ZERO, tx);
        } catch (MzsCoreException e) {
            logger.info("Investor depot not found for: " + investorId);
        }

        if (entries != null && !entries.isEmpty())
            entry = entries.get(0);

        return entry;
    }

    @Override
    public void setInvestor(InvestorDepotEntry ide, Object sharedTransaction) throws CoordinationServiceException {
        logger.info("Try to write InvestorDepotEntry: " + ide.getBudget().toString());
        TransactionReference tx = (TransactionReference)sharedTransaction;
        try {
            try {
                capi.take(investorDepotContainer, KeyCoordinator.newSelector(ide.getInvestorID().toString()), MzsConstants.RequestTimeout.TRY_ONCE, tx);
            }
            catch (MzsCoreException e1) {}
            Entry entryToUpdate = new Entry(ide, KeyCoordinator.newCoordinationData(ide.getInvestorID().toString()));
            capi.write(investorDepotContainer, MzsConstants.RequestTimeout.ZERO, tx, entryToUpdate);
        }
        catch (MzsCoreException e) {
            throw new CoordinationServiceException("Could not update InvestorDepotEntry");
        }
    }

    @Override
    public void addOrder(OrderEntry oe, Object sharedTransaction) throws CoordinationServiceException {

        TransactionReference tx = null;
        if (sharedTransaction != null)
             tx = (TransactionReference)sharedTransaction;

        try {
            logger.info("Try to write OerderEntry: " + oe.getShareID());
            //QueryCoordinator.newCoordinationData())
            capi.write(new Entry(oe), orderContainer, MzsConstants.RequestTimeout.ZERO, tx);
        } catch (MzsCoreException e) {
            logger.info("Something went wrong writing an order:" + e.getMessage());
            throw new CoordinationServiceException("Could not update OrderEntry");
        }
    }

    @Override
    public OrderEntry getOrderByTemplate(OrderEntry oe, Object sharedTransaction) {
        logger.info("Try to get order by template:" + oe.getStatus());
        TransactionReference tx = (TransactionReference)sharedTransaction;
        ArrayList<OrderEntry> entries = null;
        OrderEntry entry = null;
        try {

          ArrayList<Selector> selectors = new ArrayList<Selector>();
          selectors.add(LindaCoordinator.newSelector(oe, MzsConstants.Selecting.COUNT_ALL));
          selectors.add(RandomCoordinator.newSelector(1));

          entries = capi.take(orderContainer, selectors, MzsConstants.RequestTimeout.ZERO, tx);
        } catch (MzsCoreException e) {logger.info("Try to get order by template FAILED:" + e.getMessage()); };

        if (entries != null && !entries.isEmpty())
            entry = entries.get(0);

        return entry;
    }


    @Override
    public OrderEntry getOrderByProperties(String shareId, OrderType type, OrderStatus status, Double price, Object sharedTransaction) {
        TransactionReference tx = (TransactionReference)sharedTransaction;
        ArrayList<OrderEntry> entries = null;

        Matchmaker mShareId = Property.forName("shareID").equalTo(shareId);
        Matchmaker mType = Property.forName("type").equalTo(type);
        Matchmaker mStatus = Property.forName("status").equalTo(status);
        //Matchmaker mStatus1 = Property.forName("status").equalTo(OrderStatus.OPEN);
        //Matchmaker mStatus2 = Property.forName("status").equalTo(OrderStatus.PARTIAL);
        Matchmaker mLimit = ComparableProperty.forName("limit").lessThanOrEqualTo(price);
        if (type.equals(OrderType.BUY))
            mLimit = ComparableProperty.forName("limit").greaterThanOrEqualTo(price);

        Query q = new Query().filter(and(mShareId, mType, mStatus, mLimit)).cnt(1);
        OrderEntry entry = null;
        try {

            ArrayList<Selector> selectors = new ArrayList<Selector>();
            selectors.add(QueryCoordinator.newSelector(q, MzsConstants.Selecting.COUNT_ALL));
            selectors.add(RandomCoordinator.newSelector(1));

            entries = capi.take(orderContainer,
                    selectors,
                    MzsConstants.RequestTimeout.ZERO, tx);
        } catch (MzsCoreException e) {logger.info("Try to get order by properties FAILED:" + e.getMessage());}

        if (entries != null && !entries.isEmpty())
            entry = entries.get(0);

        return entry;
    }

    @Override
    public ArrayList<ShareEntry> readShares() {

        ArrayList<ShareEntry> entries = null;
        try {
            entries = capi.read(shareContainer, FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL), MzsConstants.RequestTimeout.TRY_ONCE, null);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
        return entries;
    }


    @Override
    public ReleaseEntry getReleaseEntry(Object sharedTransaction) {

        TransactionReference tx = (TransactionReference)sharedTransaction;
        ReleaseEntry re = null;
        try {
            ArrayList<ReleaseEntry> entries = null;
            entries = capi.take(releaseContainer, FifoCoordinator.newSelector(), MzsConstants.RequestTimeout.ZERO, tx);

            if (entries != null && !entries.isEmpty())
                re = entries.get(0);

        }
        catch (MzsCoreException e) {
            try {
                capi.rollbackTransaction(tx);
            } catch (MzsCoreException e1) {
                e1.printStackTrace();
            }
        }

        return re;
    }

    @Override
    public ShareEntry getShareEntry(String shareId, Object sharedTransaction) {

        TransactionReference tx = (TransactionReference)sharedTransaction;
        ShareEntry se = null;
        ArrayList<ShareEntry> entries = null;
        try {
            entries = capi.read(shareContainer, KeyCoordinator.newSelector(shareId), MzsConstants.RequestTimeout.ZERO, tx);
        }
        catch (MzsCoreException e) {
            logger.info("Share not found for: " + shareId);
        }
        if (entries != null && !entries.isEmpty())
            se = entries.get(0);

        return se;
    }

    @Override
    public void setShareEntry(ShareEntry se, Object sharedTransaction) throws CoordinationServiceException {

        TransactionReference tx = (TransactionReference)sharedTransaction;
        try {

            try {
                capi.take(shareContainer, KeyCoordinator.newSelector(se.getShareID()), MzsConstants.RequestTimeout.TRY_ONCE, tx);
            }
            catch (MzsCoreException e) {}

            Entry entryToUpdate = new Entry(se, KeyCoordinator.newCoordinationData(se.getShareID()));
            capi.write(shareContainer, MzsConstants.RequestTimeout.ZERO, tx, entryToUpdate);
        }
        catch (MzsCoreException e) {
            throw new CoordinationServiceException("Something went wrong writing a ShareEntry");
        }
    }

    @Override
    public void registerReleaseNotification(CoordinationListener cListener) {
        try {
            NotificationManager notificationManager = new NotificationManager(core);

            Notification notification = notificationManager.createNotification(releaseContainer,
                    new ReleaseNotificationListener(cListener),
                    Operation.WRITE);

            notifications.add(notification);

        } catch (MzsCoreException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerOrderNotification(CoordinationListener cListener) {
        try {
            NotificationManager notificationManager = new NotificationManager(core);

            Notification notification = notificationManager.createNotification(orderContainer,
                    new OrderNotificationListener(cListener),
                    Operation.WRITE);

            notifications.add(notification);

        } catch (MzsCoreException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerShareNotification(CoordinationListener cListener) {
        try {
            NotificationManager notificationManager = new NotificationManager(core);

            Notification notification = notificationManager.createNotification(shareContainer,
                    new ShareNotificationListener(cListener),
                    Operation.WRITE);

            notifications.add(notification);

        } catch (MzsCoreException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addTransaction(TransactionEntry te, Object sharedTransaction) throws CoordinationServiceException {
        TransactionReference tx = (TransactionReference)sharedTransaction;
        try {
            capi.write(new Entry(te), transactionContainer, MzsConstants.RequestTimeout.ZERO, tx);
        } catch (MzsCoreException e) {
            logger.info("Something went wrong writing a TransactionEntry");
            throw new CoordinationServiceException("Could not write a TransactionEntry");
        }
    }

    @Override
    public Object createTransaction(long timeout) {


        TransactionReference tx = null;
        try {
            tx = capi.createTransaction(timeout, new URI(CommonSpaceConfiguration.SPACE_URI));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tx;
    }

    @Override
    public void commitTransaction(Object sharedTransaction) {

        if (sharedTransaction != null) {

            TransactionReference tx = (TransactionReference)sharedTransaction;
            try {
                capi.commitTransaction(tx);
            } catch (MzsCoreException e) {
                logger.info("Transaction failed at some point");
            }

            sharedTransaction = null;
        }
    }

    @Override
    public Boolean transactionIsRunning(Object sharedTransaction) {

        return false;
    }

    @Override
    public void rollbackTransaction(Object sharedTransaction) {
        if (sharedTransaction != null) {

            TransactionReference tx = (TransactionReference)sharedTransaction;
            try {
                capi.rollbackTransaction(tx);
            } catch (MzsCoreException e) {
                logger.info("Transaction failed at some point");
            }

            sharedTransaction = null;
        }
    }

    /**
     * InvestorDepotNotificationListener
     */
    public class ReleaseNotificationListener implements NotificationListener {

        private CoordinationListener<ArrayList<ReleaseEntry>> callbackListener;

        public ReleaseNotificationListener(CoordinationListener<ArrayList<ReleaseEntry>> callbackListener) {
            this.callbackListener = callbackListener;
        }

        @Override
        public void entryOperationFinished(Notification notification, Operation operation, List<? extends Serializable> entries) {

            ArrayList<ReleaseEntry> releaseEntries = new ArrayList<ReleaseEntry>();
            for (Serializable entry : entries) {
                releaseEntries.add((ReleaseEntry)((Entry)entry).getValue());
            }
            callbackListener.onResult(releaseEntries);
        }
    }

    /**
     * OrderNotificationListener
     */
    public class OrderNotificationListener implements NotificationListener {

        private CoordinationListener<ArrayList<OrderEntry>> callbackListener;

        public OrderNotificationListener(CoordinationListener<ArrayList<OrderEntry>> callbackListener) {
            this.callbackListener = callbackListener;
        }

        @Override
        public void entryOperationFinished(Notification notification, Operation operation, List<? extends Serializable> entries) {

            ArrayList<OrderEntry> orderEntries = new ArrayList<OrderEntry>();
            for (Serializable entry : entries) {
                orderEntries.add((OrderEntry)((Entry)entry).getValue());
            }
            callbackListener.onResult(orderEntries);
        }
    }

    public class ShareNotificationListener implements NotificationListener {

        private CoordinationListener<ArrayList<ShareEntry>> callbackListener;

        public ShareNotificationListener(CoordinationListener<ArrayList<ShareEntry>> callbackListener) {
            this.callbackListener = callbackListener;
        }

        @Override
        public void entryOperationFinished(Notification notification, Operation operation, List<? extends Serializable> entries) {

            ArrayList<ShareEntry> shareEntries = new ArrayList<ShareEntry>();
            for (Serializable entry : entries) {
                shareEntries.add((ShareEntry)((Entry)entry).getValue());
            }
            callbackListener.onResult(shareEntries);
        }
    }
}
