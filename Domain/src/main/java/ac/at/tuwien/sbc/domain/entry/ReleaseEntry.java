package ac.at.tuwien.sbc.domain.entry;

import ac.at.tuwien.sbc.domain.enums.ShareType;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 25/03/15.
 */
public class ReleaseEntry extends SuperEntry implements Serializable {

    /** The company id. */
    private String companyID;

    /** The num shares. */
    private Integer numShares;

    /** The price. */
    private Double price;

    private ShareType shareType;

    /**
     * Instantiates a new release entry.
     */
    public ReleaseEntry(){}


    /**
     * Instantiates a new release entry.
     *
     * @param companyID the company id
     * @param numShares the num shares
     * @param price the price
     */
    public ReleaseEntry(String companyID, Integer numShares, Double price, ShareType shareType){
        this.companyID = companyID;
        this.numShares = numShares;
        this.price = price;
        this.shareType = shareType;
    }

    /**
     * Gets the company id.
     *
     * @return the company id
     */
    public String getCompanyID() {
        return companyID;
    }

    /**
     * Sets the company id.
     *
     * @param companyID the new company id
     */
    public void setCompanyID(String companyID) {
        this.companyID = companyID;
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

    public ShareType getShareType() {
        return shareType;
    }

    public void setShareType(ShareType shareType) {
        this.shareType = shareType;
    }
}
