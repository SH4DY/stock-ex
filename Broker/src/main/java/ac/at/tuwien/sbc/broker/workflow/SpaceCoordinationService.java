package ac.at.tuwien.sbc.broker.workflow;

import ac.at.tuwien.sbc.domain.configuration.CommonSpaceConfiguration;
import ac.at.tuwien.sbc.domain.entry.InvestorDepotEntry;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ReleaseEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.KeyCoordinator;
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

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
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

    private ArrayList<Notification> notifications = new ArrayList<Notification>();



    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(SpaceCoordinationService.class);

    @Override
    public void getInvestor(Integer investorId, CoordinationListener clistener) {

    }

    @Override
    public void setInvestor(InvestorDepotEntry ide) {

    }

    @Override
    public void addOrder(OrderEntry oe, Object sharedTransaction) {

        TransactionReference tx = (TransactionReference)sharedTransaction;
        try {
            logger.info("Try to write OerderEntry: " + oe.getShareID());
            capi.write(new Entry(oe), orderContainer, MzsConstants.RequestTimeout.ZERO, tx);
        } catch (MzsCoreException e) {
            logger.info("Something went wrong writing an order");
        }
    }

    @Override
    public void getOrders(Integer id, CoordinationListener cListener) {

    }

    @Override
    public ReleaseEntry getReleaseEntry(Object sharedTransaction) {
        logger.info("Try to get a release:" + Thread.currentThread());

        TransactionReference tx = (TransactionReference)sharedTransaction;
        ReleaseEntry re = null;
        try {
           // releaseTx = capi.createTransaction(1000, releaseContainer.getSpace());
            ArrayList<ReleaseEntry> entries = null;
            logger.info("Try to get a release TAKE");

            entries = capi.take(releaseContainer, FifoCoordinator.newSelector(), MzsConstants.RequestTimeout.ZERO, tx);
            logger.info("Try to get a release GOT");

            if (entries != null && !entries.isEmpty())
                re = entries.get(0);

            //capi.commitTransaction(releaseTx);
        }
        catch (MzsCoreException e) {
            try {
                capi.rollbackTransaction(tx);
            } catch (MzsCoreException e1) {
                e1.printStackTrace();
            }
            logger.info("TRANSACTION MAY TIMED OUT");
        }

        return re;
    }

    @Override
    public ShareEntry getShareEntry(String shareId, Object sharedTransaction) {
        logger.info("Try to get a share:");

        TransactionReference tx = (TransactionReference)sharedTransaction;
        ShareEntry se = null;
        ArrayList<ShareEntry> entries = null;

        try {
            entries = capi.read(shareContainer, KeyCoordinator.newSelector(shareId), MzsConstants.RequestTimeout.ZERO, tx);
        }
        catch (MzsCoreException e) {
            logger.info("Share not found for: " + shareId);
        }
        logger.info("Try to get a share GOT");
        if (entries != null && !entries.isEmpty())
            se = entries.get(0);


        return se;
    }

    @Override
    public void setShareEntry(ShareEntry se, Object sharedTransaction) {

        logger.info("Try to write ShareEntry: " + se.getShareID());
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
            try {
                capi.rollbackTransaction(tx);
            } catch (MzsCoreException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            logger.info("Something went wrong writing a ShareEntry");
        }
    }

    @Override
    public void registerReleaseNotification(CoordinationListener clistener) {
        try {
            NotificationManager notificationManager = new NotificationManager(core);

            Notification notification = notificationManager.createNotification(releaseContainer,
                    new ReleaseNotificationListener(clistener),
                    Operation.WRITE);

            notifications.add(notification);

        } catch (MzsCoreException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
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
        }
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
}
