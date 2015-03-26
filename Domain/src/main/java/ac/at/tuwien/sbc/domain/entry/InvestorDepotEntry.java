package ac.at.tuwien.sbc.domain.entry;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by dietl_ma on 25/03/15.
 */
public class InvestorDepotEntry implements Serializable {

    private Integer investorID;

    private Double budget;

    private HashMap<String, Integer> shareDepot;


    public InvestorDepotEntry() {}

    public InvestorDepotEntry(Integer investorID, Double budget, HashMap<String, Integer> shareDepot) {
        this.investorID = investorID;
        this.budget = budget;
        this.shareDepot = shareDepot;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public HashMap<String, Integer> getShareDepot() {
        return shareDepot;
    }

    public void setShareDepot(HashMap<String, Integer> shareDepot) {
        this.shareDepot = shareDepot;
    }

    public Integer getInvestorID() {
        return investorID;
    }

    public void setInvestorID(Integer investorID) {
        this.investorID = investorID;
    }
}
