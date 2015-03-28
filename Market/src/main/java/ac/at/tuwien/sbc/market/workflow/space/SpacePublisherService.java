package ac.at.tuwien.sbc.market.workflow.space;

import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import ac.at.tuwien.sbc.market.workflow.IMarketEventListener;
import ac.at.tuwien.sbc.market.workflow.IMarketPublisherService;
import org.mozartspaces.core.*;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
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

        //TODO add notifications to somewhere?
    }

    private class OrderAddedListener implements org.mozartspaces.notifications.NotificationListener{
        List<CoordinationListener<OrderEntry>> listeners;

        public OrderAddedListener(CoordinationListener<OrderEntry> observer) {
            listeners = new ArrayList<>();
            listeners.add(observer);
        }

        @Override
        public void entryOperationFinished(org.mozartspaces.notifications.Notification notification, Operation operation, List<? extends Serializable> entries) {
            for(Serializable entry : entries){
                for(CoordinationListener<OrderEntry> listener : listeners){
                    listener.onResult((OrderEntry)((Entry)entry).getValue());
                }
            }
        }
    }
}
