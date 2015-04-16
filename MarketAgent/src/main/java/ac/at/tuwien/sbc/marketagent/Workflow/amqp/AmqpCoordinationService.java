package ac.at.tuwien.sbc.marketagent.workflow.amqp;

import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import ac.at.tuwien.sbc.domain.enums.OrderStatus;
import ac.at.tuwien.sbc.domain.enums.OrderType;
import ac.at.tuwien.sbc.domain.messaging.RPCMessageRequest;
import ac.at.tuwien.sbc.marketagent.workflow.ICoordinationService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 07/04/15.
 */
@Service
@Profile("amqp")
public class AmqpCoordinationService implements ICoordinationService {


    /** The template. */
    @Autowired
    private RabbitTemplate template;

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.marketagent.workflow.ICoordinationService#getShares()
     */
    @Override
    public ArrayList<ShareEntry> getShares() {
        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.GET_SHARE_ENTRIES, null);
        ArrayList<ShareEntry> entries = (ArrayList<ShareEntry>)template.convertSendAndReceive("marketRPC", request);
        return entries;
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.marketagent.workflow.ICoordinationService#setShareEntry(ac.at.tuwien.sbc.domain.entry.ShareEntry)
     */
    @Override
    public void setShareEntry(ShareEntry se) {
        //delete share
        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.DELETE_SHARE_ENTRY_BY_ID, new Object[]{se.getShareID()});
        template.convertAndSend("marketRPC", request);

        //write share
        request = new RPCMessageRequest(RPCMessageRequest.Method.WRITE_SHARE_ENTRY, null, se);
        template.convertAndSend("marketRPC", request);
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.marketagent.workflow.ICoordinationService#getOrdersByProperties(java.lang.String, ac.at.tuwien.sbc.domain.enums.OrderType)
     */
    @Override
    public ArrayList<OrderEntry> getOrdersByProperties(String shareId, OrderType type) {

        //get open orders
        RPCMessageRequest request = new RPCMessageRequest(RPCMessageRequest.Method.READ_ORDER_BY_PROPERTIES,
                new Object[]{shareId, type, OrderStatus.OPEN});
        ArrayList<OrderEntry> entries = (ArrayList<OrderEntry>)template.convertSendAndReceive("marketRPC", request);

        //get partial orders
        request = new RPCMessageRequest(RPCMessageRequest.Method.READ_ORDER_BY_PROPERTIES,
                new Object[]{shareId, type, OrderStatus.PARTIAL});
        entries.addAll((ArrayList<OrderEntry>)template.convertSendAndReceive("marketRPC", request));
        return entries;
    }
}
