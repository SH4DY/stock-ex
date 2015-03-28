package ac.at.tuwien.sbc.market.workflow;

import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This Service acts as the controller layer for the model layer.
 * It enables decoupling from underlying layers (space-based, message-based...)
 * by implementing an interface and using an underlying service which gets
 * auto-injected depending on the active profile.
 */
@Service
public class Workflow implements IMarketEventListener {

    @Autowired
    IMarketPublisherService marketPublisherService;

    @Autowired
    IMarketObserver observer;

    private void setupOrderNotification() {
        marketPublisherService.registerOrderObserver(new CoordinationListener<OrderEntry>(){

            @Override
            public void onResult(OrderEntry orderEntry) {
                if(observer != null){
                    observer.onOrderAdded(orderEntry);
                }
            }
        });
    }

}
