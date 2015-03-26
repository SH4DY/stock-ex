package ac.at.tuwien.sbc.domain.entry;

import ac.at.tuwien.sbc.domain.enums.OrderStatus;
import ac.at.tuwien.sbc.domain.enums.OrderType;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by dietl_ma on 25/03/15.
 */
//RANDOM OR ANY COORDINATOR
public class OrderEntry implements Serializable {

    private UUID orderID;

    //is null if reseller it is IPO
    private Integer investorID;

    private String shareID;

    private OrderType type;

    private Double limit;

    private Integer num_total;

    private Integer num_completed;

    private OrderStatus status;

}
