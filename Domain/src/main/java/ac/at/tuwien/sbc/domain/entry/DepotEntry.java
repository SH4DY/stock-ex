package ac.at.tuwien.sbc.domain.entry;

import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;

import java.io.Serializable;
import java.util.HashMap;

// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 25/03/15.
 */
public class DepotEntry extends SuperEntry implements Serializable{

    /** The investor id. */
    private Integer investorID;

    /** The budget. */
    private Double budget;

    /** The share depot. */
    private HashMap<String, Integer> shareDepot;


    /**
     * Instantiates a new investor depot entry.
     */
    public DepotEntry() {}

    /**
     * Instantiates a new investor depot entry.
     *
     * @param investorID the investor id
     * @param budget the budget
     * @param shareDepot the share depot
     */
    public DepotEntry(Integer investorID, Double budget, HashMap<String, Integer> shareDepot) {
        this.investorID = investorID;
        this.budget = budget;
        this.shareDepot = shareDepot;
    }

    /**
     * Gets the budget.
     *
     * @return the budget
     */
    public Double getBudget() {
        return budget;
    }

    /**
     * Sets the budget.
     *
     * @param budget the new budget
     */
    public void setBudget(Double budget) {
        this.budget = budget;
    }

    /**
     * Gets the share depot.
     *
     * @return the share depot
     */
    public HashMap<String, Integer> getShareDepot() {
        return shareDepot;
    }

    /**
     * Sets the share depot.
     *
     * @param shareDepot the share depot
     */
    public void setShareDepot(HashMap<String, Integer> shareDepot) {
        this.shareDepot = shareDepot;
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


}
