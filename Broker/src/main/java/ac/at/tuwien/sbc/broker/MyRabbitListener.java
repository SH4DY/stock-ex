package ac.at.tuwien.sbc.broker;

import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by dietl_ma on 18/03/15.
 */
public class MyRabbitListener {

   // private SimpleMessageListenerContainer container;

    public MyRabbitListener(SimpleMessageListenerContainer c) {
       // container = c;
    }
    @Transactional(timeout=10000)
    public void handleMessage(Object foo) throws InterruptedException {

        String i = null;
        System.out.println("FROM RABBIT:" + foo.toString());
       // System.out.println("EXCEPTION:" + i.toString());
        //container.stop();
       // while(true) {}
    }
}
