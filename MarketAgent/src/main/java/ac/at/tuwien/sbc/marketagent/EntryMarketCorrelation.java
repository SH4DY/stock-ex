package ac.at.tuwien.sbc.marketagent;

import ac.at.tuwien.sbc.domain.entry.ShareEntry;

/**
 * Created by dietl_ma on 31/05/15.
 */
public class EntryMarketCorrelation<T> {
    private T entry;
    private String market;

    public EntryMarketCorrelation() {}

    public EntryMarketCorrelation(T entry, String market) {
        this.entry = entry;
        this.market = market;
    }

    public T getEntry() {
        return entry;
    }

    public void setShareEntry(T entry) {
        this.entry = entry;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }
}
