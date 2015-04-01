package ac.at.tuwien.sbc.domain.entry;

import org.springframework.scheduling.annotation.Scheduled;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by dietl_ma on 25/03/15.
 */
//FIFO CONTAINER
public class TransactionEntry extends SuperEntry implements Serializable {

    private String transactionID;

    private Integer brokerID;

    private Integer buyerID;

    private Integer sellerID;

    private String shareID;

    private UUID sellOrderID;

    private UUID buyOrderID;

    private Double price;

    private Integer numShares;

    private Double sumPrice;

    private Double provision;

    public TransactionEntry() {}

    public TransactionEntry(String transactionID, Integer brokerID, Integer buyerID, Integer sellerID, String shareID, UUID sellOrderID, UUID buyOrderID, Double price, Integer numShares, Double sumPrice, Double provision) {
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

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public Integer getBrokerID() {
        return brokerID;
    }

    public void setBrokerID(Integer brokerID) {
        this.brokerID = brokerID;
    }

    public Integer getBuyerID() {
        return buyerID;
    }

    public void setBuyerID(Integer buyerID) {
        this.buyerID = buyerID;
    }

    public Integer getSellerID() {
        return sellerID;
    }

    public void setSellerID(Integer sellerID) {
        this.sellerID = sellerID;
    }

    public String getShareID() {
        return shareID;
    }

    public void setShareID(String shareID) {
        this.shareID = shareID;
    }

    public UUID getSellOrderID() {
        return sellOrderID;
    }

    public void setSellOrderID(UUID sellOrderID) {
        this.sellOrderID = sellOrderID;
    }

    public UUID getBuyOrderID() {
        return buyOrderID;
    }

    public void setBuyOrderID(UUID buyOrderID) {
        this.buyOrderID = buyOrderID;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getNumShares() {
        return numShares;
    }

    public void setNumShares(Integer numShares) {
        this.numShares = numShares;
    }

    public Double getSumPrice() {
        return sumPrice;
    }

    public void setSumPrice(Double sumPrice) {
        this.sumPrice = sumPrice;
    }

    public Double getProvision() {
        return provision;
    }

    public void setProvision(Double provision) {
        this.provision = provision;
    }
}
