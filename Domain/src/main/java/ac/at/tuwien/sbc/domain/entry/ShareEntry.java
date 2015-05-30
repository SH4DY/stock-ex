package ac.at.tuwien.sbc.domain.entry;

import ac.at.tuwien.sbc.domain.enums.ShareType;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 25/03/15.
 */
//SEPARATE CONTAINER
public  class ShareEntry extends SuperEntry implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The share id. */
    private  String shareID;

    /** The num shares. */
    private  Integer numShares;

    /** The price. */
    private  Double price;

    private ShareType shareType;

    /**
     * Instantiates a new share entry.
     */
    public ShareEntry() {};

    /**
     * Instantiates a new share entry.
     *
     * @param shareID the share id
     * @param numShares the num shares
     * @param price the price
     */
    public ShareEntry(String  shareID,  Integer numShares,  Double price, ShareType shareType) {
        this.numShares = numShares;
        this.price = price;
        this.shareID = shareID;
        this.shareType = shareType;
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
     * Sets the num shares.
     *
     * @param numShares the new num shares
     */
    public void setNumShares(Integer numShares) {
        this.numShares = numShares;
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
     * Gets the share id.
     *
     * @return the share id
     */
    public String getShareID() {
        return shareID;
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
     * Gets the price.
     *
     * @return the price
     */
    public Double getPrice() {
        return price;
    }

    public ShareType getShareType() {
        return shareType;
    }

    public void setShareType(ShareType shareType) {
        this.shareType = shareType;
    }

    //We override equality and hashes because
    //the shareID is the only property of uniqueness
    //Main reason: GUI shall display every share
    //only ONCE, even after updating the price.
    //GUI uses a HashSet in the background.
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShareEntry)) return false;

        ShareEntry that = (ShareEntry) o;

        return shareID.equals(that.getShareID());

    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return shareID.hashCode();
    }

}
