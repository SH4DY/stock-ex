package ac.at.tuwien.sbc.domain.entry;

import java.io.Serializable;

/**
 * Created by dietl_ma on 25/03/15.
 */
//SEPARATE CONTAINER
public  class ShareEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    private  String shareID;

    private  Integer numShares;

    private  Double price;

    public ShareEntry(String  shareID,  Integer numShares,  Double price) {
        this.numShares = numShares;
        this.price = price;
        this.shareID = shareID;
    }

    public String getShareID() {
        return shareID;
    }

    public Integer getNumShares() {
        return numShares;
    }

    public Double getPrice() {
        return price;
    }
}
