package ac.at.tuwien.sbc.market.workflow.amqp;

/**
 * Created by shady on 12/04/15.
 */
import ac.at.tuwien.sbc.domain.entry.InvestorDepotEntry;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
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
public class AmqpMessageHandler {

    @Autowired
    private AmqpPublisherService amqpPublisherService;

    public void onShareEntryNotification(ArrayList<ShareEntry> list) {
        amqpPublisherService.onShareEntryNotification(list);
    }
}
