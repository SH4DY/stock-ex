package ac.at.tuwien.sbc.market.workflow.space;

import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import ac.at.tuwien.sbc.market.workflow.IMarketServiceListener;
import ac.at.tuwien.sbc.market.workflow.IMarketPublisherService;
import org.mozartspaces.capi3.LindaCoordinator;
import org.mozartspaces.core.*;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * Created by shady on 28/03/15.
 */
@Service
@Profile("space")
public class SpacePublisherService implements IMarketPublisherService {

    @Autowired
    Capi capi;

    @Autowired
    MzsCore core;

    @Autowired
    @Qualifier("orderContainer")
    ContainerReference orderContainer;

    IMarketServiceListener listener;

    private static final Logger logger = LoggerFactory.getLogger(SpacePublisherService.class);

    @Override
    public void registerListener(IMarketServiceListener listener){
        this.listener = listener;
    }

    @Override
    public void registerOrderObserver(CoordinationListener<OrderEntry> coordinationListener) {
        NotificationManager nm = new NotificationManager(core);
        Notification not = null;
        try {
            not = nm.createNotification(orderContainer,
                    new OrderAddedListener(coordinationListener),
                    Operation.WRITE);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //TODO what to do with these notifications?
    }

    @Override
    public void getOrders(CoordinationListener listener){
        List<OrderEntry> readEntries = null;
        OrderEntry template = new OrderEntry(null, null, null, null, null, null, null, null);
        try {
            readEntries = capi.read(orderContainer, LindaCoordinator.newSelector(template,MzsConstants.Selecting.COUNT_ALL), MzsConstants.RequestTimeout.DEFAULT, null);
        } catch (MzsCoreException e) {
            logger.error("Exception while trying to delete all entries from orderContainer");
        }

        if(readEntries != null && !readEntries.isEmpty()){
            listener.onResult(readEntries);
        }
        else{
            listener.onResult(null);
        }
    }

    private class OrderAddedListener implements org.mozartspaces.notifications.NotificationListener{
        CoordinationListener<OrderEntry> listener;

        public OrderAddedListener(CoordinationListener<OrderEntry> listener) {
            this.listener = listener;
        }

        @Override
        public void entryOperationFinished(org.mozartspaces.notifications.Notification notification, Operation operation, List<? extends Serializable> entries) {
            for(Serializable entry : entries){
                    listener.onResult((OrderEntry)((Entry)entry).getValue());
            }
        }
    }
}
