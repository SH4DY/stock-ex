package ac.at.tuwien.sbc.domain.entry;

import java.io.Serializable;

/**
 * Created by dietl_ma on 25/03/15.
 */
public class ReleaseEntry extends SuperEntry implements Serializable {

    private String companyID;

    private Integer numShares;

    private Double price;

    public ReleaseEntry(){}


    public ReleaseEntry(String companyID, Integer numShares, Double price){
        this.companyID = companyID;
        this.numShares = numShares;
        this.price = price;
    }

    public String getCompanyID() {
        return companyID;
    }

    public void setCompanyID(String companyID) {
        this.companyID = companyID;
    }

    public Integer getNumShares() {
        return numShares;
    }

    public void setNumShares(Integer numShares) {
        this.numShares = numShares;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
