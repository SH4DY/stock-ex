package ac.at.tuwien.sbc.investor.workflow.amqp;

import ac.at.tuwien.sbc.domain.entry.DepotEntry;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 01/04/15.
 */

public class MessageHandler {

    /** The amqp coordination service. */
    //@Autowired
    private AmqpCoordinationService amqpCoordinationService;

    //@Autowired
    private ApplicationContext context;

    private String market;


    public MessageHandler() {}

    public MessageHandler(String market, ApplicationContext context) {
        this.market = market;
        this.context = context;
        this.amqpCoordinationService = context.getBean(AmqpCoordinationService.class);
    }
    /**
     * On investor entry notification.
     *
     * @param list the list
     */
    public void onDepotEntryNotification(ArrayList<DepotEntry> list) {
        amqpCoordinationService.onDepotEntryNotification(list, market);
    }

    /**
     * On share entry notification.
     *
     * @param list the list
     */
    public void onShareEntryNotification(ArrayList<ShareEntry> list) {
        amqpCoordinationService.onShareEntryNotification(list, market);
    }

    /**
     * On order entry notification.
     *
     * @param list the list
     */
    public void onOrderEntryNotification(ArrayList<OrderEntry> list) {
        amqpCoordinationService.onOrderEntryNotification(list, market);
    }
}
