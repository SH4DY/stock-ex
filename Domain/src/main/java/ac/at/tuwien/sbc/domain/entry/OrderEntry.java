package ac.at.tuwien.sbc.domain.entry;

import ac.at.tuwien.sbc.domain.enums.OrderStatus;
import ac.at.tuwien.sbc.domain.enums.OrderType;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;

import java.io.Serializable;
import java.util.UUID;

// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 25/03/15.
 */
//RANDOM OR ANY COORDINATOR
public class OrderEntry extends SuperEntry implements Serializable {

    /** The order id. */
    private UUID orderID;
    //is null if reseller it is IPO
    /** The investor id. */
    private Integer investorID;

    /** The share id. */
    private String shareID;

    /** The type. */
    private OrderType type;

    /** The limit. */
    private Double limit;

    /** The num total. */
    private Integer numTotal;

    /** The num completed. */
    private Integer numCompleted;

    /** The status. */
    private OrderStatus status;

    private Boolean prioritized;

    /**
     * Instantiates a new order entry.
     */
    public OrderEntry() {}

    /**
     * Instantiates a new order entry.
     *
     * @param orderID the order id
     * @param investorID the investor id
     * @param shareID the share id
     * @param type the type
     * @param limit the limit
     * @param numTotal the num total
     * @param numCompleted the num completed
     * @param status the status
     */
    public OrderEntry(UUID orderID, Integer investorID, String shareID, OrderType type, Double limit, Integer numTotal, Integer numCompleted, OrderStatus status, Boolean prioritized) {
        this.orderID = orderID;
        this.investorID = investorID;
        this.shareID = shareID;
        this.type = type;
        this.limit = limit;
        this.numTotal = numTotal;
        this.numCompleted = numCompleted;
        this.status = status;
        this.prioritized = prioritized;
    }

    /**
     * Gets the order id.
     *
     * @return the order id
     */
    public UUID getOrderID() {
        return orderID;
    }

    /**
     * Sets the order id.
     *
     * @param orderID the new order id
     */
    public void setOrderID(UUID orderID) {
        this.orderID = orderID;
    }

    /**
     * Gets the investor id.
     *
     * @return the investor id
     */
    public Integer getInvestorID() {
        return investorID;
    }

    /**
     * Sets the investor id.
     *
     * @param investorID the new investor id
     */
    public void setInvestorID(Integer investorID) {
        this.investorID = investorID;
    }

    /**
     * Gets the share id.
     *
     * @return the share id
     */
    public String getShareID() {
        return shareID;
    }

    /**
     * Sets the share id.
     *
     * @param shareID the new share id
     */
    public void setShareID(String shareID) {
        this.shareID = shareID;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public OrderType getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type the new type
     */
    public void setType(OrderType type) {
        this.type = type;
    }

    /**
     * Gets the limit.
     *
     * @return the limit
     */
    public Double getLimit() {
        return limit;
    }

    /**
     * Sets the limit.
     *
     * @param limit the new limit
     */
    public void setLimit(Double limit) {
        this.limit = limit;
    }

    /**
     * Gets the num total.
     *
     * @return the num total
     */
    public Integer getNumTotal() {
        return numTotal;
    }

    /**
     * Sets the num total.
     *
     * @param numTotal the new num total
     */
    public void setNumTotal(Integer numTotal) {
        this.numTotal = numTotal;
    }

    /**
     * Gets the num completed.
     *
     * @return the num completed
     */
    public Integer getNumCompleted() {
        return numCompleted;
    }

    /**
     * Sets the num completed.
     *
     * @param num_completed the new num completed
     */
    public void setNumCompleted(Integer num_completed) {
        this.numCompleted = num_completed;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public OrderStatus getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the new status
     */
    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Boolean getPrioritized() {
        return prioritized;
    }

    public void setPrioritized(Boolean prioritized) {
        this.prioritized = prioritized;
    }
}
