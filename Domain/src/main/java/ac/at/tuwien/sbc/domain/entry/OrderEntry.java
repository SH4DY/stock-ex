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

    private Integer numTotal;

    private Integer numCompleted;

    private OrderStatus status;

    public OrderEntry() {}

    public OrderEntry(UUID orderID, Integer investorID, String shareID, OrderType type, Double limit, Integer numTotal, Integer numCompleted, OrderStatus status) {
        this.orderID = orderID;
        this.investorID = investorID;
        this.shareID = shareID;
        this.type = type;
        this.limit = limit;
        this.numTotal = numTotal;
        this.numCompleted = numCompleted;
        this.status = status;
    }

    public UUID getOrderID() {
        return orderID;
    }

    public void setOrderID(UUID orderID) {
        this.orderID = orderID;
    }

    public Integer getInvestorID() {
        return investorID;
    }

    public void setInvestorID(Integer investorID) {
        this.investorID = investorID;
    }

    public String getShareID() {
        return shareID;
    }

    public void setShareID(String shareID) {
        this.shareID = shareID;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public Double getLimit() {
        return limit;
    }

    public void setLimit(Double limit) {
        this.limit = limit;
    }

    public Integer getNumTotal() {
        return numTotal;
    }

    public void setNumTotal(Integer numTotal) {
        this.numTotal = numTotal;
    }

    public Integer getNumCompleted() {
        return numCompleted;
    }

    public void setNumCompleted(Integer num_completed) {
        this.numCompleted = num_completed;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
