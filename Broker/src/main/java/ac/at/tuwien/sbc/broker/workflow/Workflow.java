package ac.at.tuwien.sbc.broker.workflow;

import ac.at.tuwien.sbc.domain.entry.InvestorDepotEntry;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ReleaseEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import ac.at.tuwien.sbc.domain.enums.OrderStatus;
import ac.at.tuwien.sbc.domain.enums.OrderType;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by dietl_ma on 27/03/15.
 */
@Service
public class Workflow {
    @Value("${id}")
    private Integer brokerId;

    @Autowired
    private ICoordinationService coordinationService;

    private Thread releaseRequestThread;

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(Workflow.class);

    @PostConstruct
    private void onPostConstruct() {
        initReleaseRequestHandling();

        handleReleaseRequests();
    }

    private void initReleaseRequestHandling() {
        coordinationService.registerReleaseNotification(new CoordinationListener<ArrayList<ReleaseEntry>>() {
            @Override
            public void onResult(ArrayList<ReleaseEntry> releaseEntries) {

                logger.info("on ReleaseEntry notification" );
                handleReleaseRequests();
            }
        });
    }

    private void handleReleaseRequests() {
        Object sharedTransaction = coordinationService.createTransaction(1000);
        ReleaseEntry releaseEntry = coordinationService.getReleaseEntry(sharedTransaction);

        if (releaseEntry == null) {

            return;
        }
        //update or create share
        ShareEntry shareEntry = coordinationService.getShareEntry(releaseEntry.getCompanyID(), sharedTransaction);

        if (shareEntry == null)
            shareEntry = new ShareEntry(releaseEntry.getCompanyID(), releaseEntry.getNumShares(), releaseEntry.getPrice());
        else {
            shareEntry.setNumShares(shareEntry.getNumShares()+releaseEntry.getNumShares());
        }

        coordinationService.setShareEntry(shareEntry, sharedTransaction);

        //add order
        OrderEntry oe = new OrderEntry(UUID.randomUUID(),
                0,
                releaseEntry.getCompanyID(),
                OrderType.SELL,
                0.0,
                releaseEntry.getNumShares(),
                0,
                OrderStatus.OPEN);

        coordinationService.addOrder(oe, sharedTransaction);

        coordinationService.commitTransaction(sharedTransaction);

    }


}
