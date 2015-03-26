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
}
