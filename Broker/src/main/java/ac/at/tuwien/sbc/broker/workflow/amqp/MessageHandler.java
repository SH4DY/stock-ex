package ac.at.tuwien.sbc.broker.workflow.amqp;

import ac.at.tuwien.sbc.domain.entry.InvestorDepotEntry;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ReleaseEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 01/04/15.
 */
@Service
@Profile("amqp")
public class MessageHandler {

    /** The amqp coordination service. */
    @Autowired
    private AmqpCoordinationService amqpCoordinationService;

    /**
     * On investor entry notification.
     *
     * @param list the list
     */
    public void onInvestorEntryNotification(ArrayList<InvestorDepotEntry> list) {
        amqpCoordinationService.onInvestorEntryNotification(list);
    }

    /**
     * On share entry notification.
     *
     * @param list the list
     */
    public void onShareEntryNotification(ArrayList<ShareEntry> list) {
        amqpCoordinationService.onShareEntryNotification(list);
    }

    /**
     * On order entry notification.
     *
     * @param list the list
     */
    public void onOrderEntryNotification(ArrayList<OrderEntry> list) {
        amqpCoordinationService.onOrderEntryNotification(list);
    }

    /**
     * On release entry notification.
     *
     * @param list the list
     */
    public void onReleaseEntryNotification(ArrayList<ReleaseEntry> list) {
        amqpCoordinationService.onReleaseEntryNotification(list);
    }
}
