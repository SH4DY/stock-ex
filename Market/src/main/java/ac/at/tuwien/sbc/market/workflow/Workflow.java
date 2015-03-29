package ac.at.tuwien.sbc.market.workflow;

import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.List;

/**
 * This Service acts as the controller layer for the model layer.
 * It enables decoupling from underlying layers (space-based, message-based...)
 * by implementing an interface and using an underlying service which gets
 * auto-injected depending on the active profile.
 */
@Service
public class Workflow implements IMarketServiceListener {

    @Autowired
    IMarketPublisherService marketPublisherService;

    @Autowired
    IMarketObserver observer;

    /**
     * 1. Supply the GUI with the initial existing orders (getAllOrders())
     * 2 .Connect the GUI to the profile-specific backend (setup...Notification())
     */
    @PostConstruct
    private void postConstruct(){
        getAllOrders();
        setupOrderNotification();
    }
    private void getAllOrders(){
        marketPublisherService.getOrders(new CoordinationListener<List<OrderEntry>>() {
            @Override
            public void onResult(List<OrderEntry> entries) {
                if(entries != null){
                    for(OrderEntry entry : entries){
                        if(observer != null){
                            observer.onOrderAdded(entry);
                        }
                    }
                }
            }
        });
    }

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
