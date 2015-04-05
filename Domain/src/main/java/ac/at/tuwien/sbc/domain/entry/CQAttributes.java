package ac.at.tuwien.sbc.domain.entry;

import ac.at.tuwien.sbc.domain.enums.OrderStatus;
import ac.at.tuwien.sbc.domain.enums.OrderType;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;

import java.util.UUID;

/**
 * Created by dietl_ma on 01/04/15.
 */
public class CQAttributes {
    //CQEngine attribute for InvestorDepotEntry
    public static final Attribute<InvestorDepotEntry, Integer> INVESTOR_INVESTOR_ID = new SimpleAttribute<InvestorDepotEntry, Integer>("investorID") {
        public Integer getValue(InvestorDepotEntry investorDepotEntry) { return investorDepotEntry.getInvestorID(); }
    };

    //CQEngine attribute for OrderEntry

    public static final Attribute<OrderEntry, UUID> ORDER_ORDER_ID= new SimpleAttribute<OrderEntry, UUID>("orderID") {
        public UUID getValue(OrderEntry orderEntry) { return orderEntry.getOrderID(); }
    };

    public static final Attribute<OrderEntry, String> ORDER_SHARE_ID = new SimpleAttribute<OrderEntry, String>("shareID") {
        public String getValue(OrderEntry orderEntry) { return orderEntry.getShareID(); }
    };

    public static final Attribute<OrderEntry, Integer> ORDER_INVESTOR_ID = new SimpleAttribute<OrderEntry, Integer>("investorID") {
        public Integer getValue(OrderEntry orderEntry) { return orderEntry.getInvestorID(); }
    };

    public static final Attribute<OrderEntry, OrderType> ORDER_TYPE = new SimpleAttribute<OrderEntry, OrderType>("type") {
        public OrderType getValue(OrderEntry orderEntry) { return orderEntry.getType(); }
    };

    public static final Attribute<OrderEntry, OrderStatus> ORDER_STATUS = new SimpleAttribute<OrderEntry, OrderStatus>("status") {
        public OrderStatus getValue(OrderEntry orderEntry) { return orderEntry.getStatus(); }
    };

    public static final Attribute<OrderEntry, Double> ORDER_LIMIT = new SimpleAttribute<OrderEntry, Double>("limit") {
        public Double getValue(OrderEntry orderEntry) { return orderEntry.getLimit(); }
    };

    //CQEngine attribute for ShareEntry
    public static final Attribute<ShareEntry, String> SHARE_SHARE_ID = new SimpleAttribute<ShareEntry, String>("shareID") {
        public String getValue(ShareEntry shareEntry) { return shareEntry.getShareID();}
    };

}