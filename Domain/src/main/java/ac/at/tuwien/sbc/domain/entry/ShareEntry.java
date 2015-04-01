package ac.at.tuwien.sbc.domain.entry;

import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;

import java.io.Serializable;

/**
 * Created by dietl_ma on 25/03/15.
 */
//SEPARATE CONTAINER
public  class ShareEntry extends SuperEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    private  String shareID;

    private  Integer numShares;

    private  Double price;

    public ShareEntry(String  shareID,  Integer numShares,  Double price) {
        this.numShares = numShares;
        this.price = price;
        this.shareID = shareID;
    }

    public void setShareID(String shareID) {
        this.shareID = shareID;
    }

    public void setNumShares(Integer numShares) {
        this.numShares = numShares;
    }

    public void setPrice(Double price) {
        this.price = price;
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
