package ac.at.tuwien.sbc.domain.entry;

import java.io.Serializable;
import java.util.UUID;

// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 25/03/15.
 */
//FIFO CONTAINER
public class TransactionEntry extends SuperEntry implements Serializable {

    /** The transaction id. */
    private String transactionID;

    /** The broker id. */
    private Integer brokerID;

    /** The buyer id. */
    private String buyerID;

    /** The seller id. */
    private String sellerID;

    /** The share id. */
    private String shareID;

    /** The sell order id. */
    private UUID sellOrderID;

    /** The buy order id. */
    private UUID buyOrderID;

    /** The price. */
    private Double price;

    /** The num shares. */
    private Integer numShares;

    /** The sum price. */
    private Double sumPrice;

    /** The provision. */
    private Double provision;

    /**
     * Instantiates a new transaction entry.
     */
    public TransactionEntry() {}

    /**
     * Instantiates a new transaction entry.
     *
     * @param transactionID the transaction id
     * @param brokerID the broker id
     * @param buyerID the buyer id
     * @param sellerID the seller id
     * @param shareID the share id
     * @param sellOrderID the sell order id
     * @param buyOrderID the buy order id
     * @param price the price
     * @param numShares the num shares
     * @param sumPrice the sum price
     * @param provision the provision
     */
    public TransactionEntry(String transactionID, Integer brokerID, String buyerID, String sellerID, String shareID, UUID sellOrderID, UUID buyOrderID, Double price, Integer numShares, Double sumPrice, Double provision) {
        this.transactionID = transactionID;
        this.brokerID = brokerID;
        this.buyerID = buyerID;
        this.sellerID = sellerID;
        this.shareID = shareID;
        this.sellOrderID = sellOrderID;
        this.buyOrderID = buyOrderID;
        this.price = price;
        this.numShares = numShares;
        this.sumPrice = sumPrice;
        this.provision = provision;
    }

    /**
     * Gets the transaction id.
     *
     * @return the transaction id
     */
    public String getTransactionID() {
        return transactionID;
    }

    /**
     * Sets the transaction id.
     *
     * @param transactionID the new transaction id
     */
    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    /**
     * Gets the broker id.
     *
     * @return the broker id
     */
    public Integer getBrokerID() {
        return brokerID;
    }

    /**
     * Sets the broker id.
     *
     * @param brokerID the new broker id
     */
    public void setBrokerID(Integer brokerID) {
        this.brokerID = brokerID;
    }

    /**
     * Gets the buyer id.
     *
     * @return the buyer id
     */
    public String getBuyerID() {
        return buyerID;
    }

    /**
     * Sets the buyer id.
     *
     * @param buyerID the new buyer id
     */
    public void setBuyerID(String buyerID) {
        this.buyerID = buyerID;
    }

    /**
     * Gets the seller id.
     *
     * @return the seller id
     */
    public String getSellerID() {
        return sellerID;
    }

    /**
     * Sets the seller id.
     *
     * @param sellerID the new seller id
     */
    public void setSellerID(String sellerID) {
        this.sellerID = sellerID;
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
     * Gets the sell order id.
     *
     * @return the sell order id
     */
    public UUID getSellOrderID() {
        return sellOrderID;
    }

    /**
     * Sets the sell order id.
     *
     * @param sellOrderID the new sell order id
     */
    public void setSellOrderID(UUID sellOrderID) {
        this.sellOrderID = sellOrderID;
    }

    /**
     * Gets the buy order id.
     *
     * @return the buy order id
     */
    public UUID getBuyOrderID() {
        return buyOrderID;
    }

    /**
     * Sets the buy order id.
     *
     * @param buyOrderID the new buy order id
     */
    public void setBuyOrderID(UUID buyOrderID) {
        this.buyOrderID = buyOrderID;
    }

    /**
     * Gets the price.
     *
     * @return the price
     */
    public Double getPrice() {
        return price;
    }

    /**
     * Sets the price.
     *
     * @param price the new price
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * Gets the num shares.
     *
     * @return the num shares
     */
    public Integer getNumShares() {
        return numShares;
    }

    /**
     * Sets the num shares.
     *
     * @param numShares the new num shares
     */
    public void setNumShares(Integer numShares) {
        this.numShares = numShares;
    }

    /**
     * Gets the sum price.
     *
     * @return the sum price
     */
    public Double getSumPrice() {
        return sumPrice;
    }

    /**
     * Sets the sum price.
     *
     * @param sumPrice the new sum price
     */
    public void setSumPrice(Double sumPrice) {
        this.sumPrice = sumPrice;
    }

    /**
     * Gets the provision.
     *
     * @return the provision
     */
    public Double getProvision() {
        return provision;
    }

    /**
     * Sets the provision.
     *
     * @param provision the new provision
     */
    public void setProvision(Double provision) {
        this.provision = provision;
    }
}
