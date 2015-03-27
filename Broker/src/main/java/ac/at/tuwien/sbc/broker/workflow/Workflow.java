package ac.at.tuwien.sbc.broker.workflow;

import ac.at.tuwien.sbc.domain.entry.InvestorDepotEntry;
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

/**
 * Created by dietl_ma on 27/03/15.
 */
@Service
public class Workflow {
    @Value("${id}")
    private Integer brokerId;

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private TopicExchange exchange;

    @Autowired
    private RabbitAdmin rabbitAdmin;
    //@Autowired
    //private ICoordinationService coordinationService;

    @PostConstruct
    public void test() {

       /* Queue queue = new Queue("stockqueue_test1", true);
        BindingBuilder.bind(queue).to(exchange).with("stockqueue_test1");

        rabbitAdmin.declareQueue(queue);
        //rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with("stockqueue123"));
        //rabbitAdmin.deleteQueue(BindingBuilder.bind(queue).to(exchange).with("stockqueue123"));
        //template.convertAndSend("stockqueue123", "blalalala");
        System.out.println("Message send 1");
        InvestorDepotEntry e = new InvestorDepotEntry(10, 2.0, null);
        Object o = template.convertSendAndReceive("stockqueue_test1", e);


        System.out.println("Message send:" + o.toString());*/
    }

}
