package ac.at.tuwien.sbc.company.workflow.amqp;

import ac.at.tuwien.sbc.company.workflow.IReleaseService;
import ac.at.tuwien.sbc.domain.configuration.CommonRabbitConfiguration;
import ac.at.tuwien.sbc.domain.entry.ReleaseEntry;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by dietl_ma on 31/03/15.
 */
@Service
@Profile("amqp")
public class AmqpReleaseService implements IReleaseService {

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private ApplicationContext ctx;

    @Override
    public void makeRelease(ReleaseEntry rls) {

        template.convertAndSend(CommonRabbitConfiguration.RELEASE_ENTRY_QUEUE,
                rls);
    }


}
