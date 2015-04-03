package ac.at.tuwien.sbc.broker.workflow.amqp;

import ac.at.tuwien.sbc.domain.entry.InvestorDepotEntry;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ReleaseEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Created by dietl_ma on 01/04/15.
 */
@Service
@Profile("amqp")
public class MessageHandler {

    @Autowired
    private AmqpCoordinationService amqpCoordinationService;

    public void onInvestorEntryNotification(ArrayList<InvestorDepotEntry> list) {
        amqpCoordinationService.onInvestorEntryNotification(list);
    }

    public void onShareEntryNotification(ArrayList<ShareEntry> list) {
        amqpCoordinationService.onShareEntryNotification(list);
    }

    public void onOrderEntryNotification(ArrayList<OrderEntry> list) {
        amqpCoordinationService.onOrderEntryNotification(list);
    }

    public void onReleaseEntryMessage(ArrayList<ReleaseEntry> list) {
        amqpCoordinationService.onReleaseEntryMessage(list);
    }
}
