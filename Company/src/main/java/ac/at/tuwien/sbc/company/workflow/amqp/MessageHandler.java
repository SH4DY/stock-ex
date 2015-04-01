package ac.at.tuwien.sbc.company.workflow.amqp;

import ac.at.tuwien.sbc.domain.entry.ReleaseEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import java.util.ArrayList;

/**
 * Created by dietl_ma on 01/04/15.
 */
public class MessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    public void handleMessage(ArrayList<ReleaseEntry> message) {
        logger.info("NEW MESSAGE RECEIVED: " + message.size());
    }

    public void handleMessage(String message) {
        logger.info("NEW MESSAGE RECEIVED: " + message);
    }
}
