package ac.at.tuwien.sbc.company.workflow.amqp;

import ac.at.tuwien.sbc.company.workflow.IReleaseService;
import ac.at.tuwien.sbc.domain.configuration.CommonRabbitConfiguration;
import ac.at.tuwien.sbc.domain.entry.ReleaseEntry;
import ac.at.tuwien.sbc.domain.messaging.RPCMessageRequest;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 31/03/15.
 */
@Service
@Profile("amqp")
public class AmqpReleaseService implements IReleaseService {

    /** The template. */
    @Autowired
    private RabbitTemplate template;

    @Autowired
    @Qualifier("exchangeKey")
    private String exchangeKey;

    /** The ctx. */
    @Autowired
    private ApplicationContext ctx;

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.company.workflow.IReleaseService#makeRelease(ac.at.tuwien.sbc.domain.entry.ReleaseEntry)
     */
    @Override
    public void makeRelease(ReleaseEntry rls) {
        //write release entry
        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.WRITE_RELEASE_ENTRY, null, rls);
        template.convertAndSend(exchangeKey, CommonRabbitConfiguration.MARKET_RPC, request);
    }
}
