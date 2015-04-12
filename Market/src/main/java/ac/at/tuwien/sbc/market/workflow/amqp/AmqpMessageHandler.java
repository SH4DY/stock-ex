package ac.at.tuwien.sbc.market.workflow.amqp;

import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import ac.at.tuwien.sbc.domain.entry.TransactionEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * The class {@link ac.at.tuwien.sbc.market.configuration.RabbitConfiguration}
 * references this handler for callbacks coming from the underlying AMQP server.
 * Created by dietl_ma on 01/04/15.
 */
@Service
@Profile("amqp")
public class AmqpMessageHandler {

    @Autowired
    private AmqpPublisherService amqpPublisherService;

    public void onShareEntryNotification(ArrayList<ShareEntry> list) {
        amqpPublisherService.onShareEntryNotification(list);
    }

    public void onOrderEntryNotification(ArrayList<OrderEntry> list) {
        amqpPublisherService.onOrderEntryNotification(list);
    }

    public void onTransactionEntryNotification(ArrayList<TransactionEntry> list) {
        amqpPublisherService.onTransactionEntryNotification(list);
    }
}
