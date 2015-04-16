package ac.at.tuwien.sbc.market.workflow.amqp;

import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import ac.at.tuwien.sbc.domain.entry.TransactionEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The class {@link ac.at.tuwien.sbc.market.configuration.RabbitConfiguration}
 * references this handler for callbacks coming from the underlying AMQP server.
 * Created by dietl_ma on 01/04/15.
 */
@Service
@Profile("amqp")
public class AmqpMessageHandler {

    /** The amqp publisher service. */
    @Autowired
    private AmqpPublisherService amqpPublisherService;

    /**
     * On share entry notification.
     *
     * @param list the list
     */
    public void onShareEntryNotification(ArrayList<ShareEntry> list) {
        amqpPublisherService.onShareEntryNotification(list);
    }

    /**
     * On order entry notification.
     *
     * @param list the list
     */
    public void onOrderEntryNotification(ArrayList<OrderEntry> list) {
        amqpPublisherService.onOrderEntryNotification(list);
    }

    /**
     * On transaction entry notification.
     *
     * @param list the list
     */
    public void onTransactionEntryNotification(ArrayList<TransactionEntry> list) {
        amqpPublisherService.onTransactionEntryNotification(list);
    }
}
