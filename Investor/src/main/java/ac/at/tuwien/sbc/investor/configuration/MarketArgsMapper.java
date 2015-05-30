package ac.at.tuwien.sbc.investor.configuration;

import ac.at.tuwien.sbc.domain.configuration.IMarketArgsMapper;
import org.springframework.stereotype.Component;

/**
 * Created by dietl_ma on 29/05/15.
 */
@Component
public class MarketArgsMapper implements IMarketArgsMapper <MarketArgsMapper.MarketArgs> {
    @Override
    public MarketArgsMapper.MarketArgs getObjectForArgs(Object[] args) {
        return new MarketArgs(args);
    }

    public class MarketArgs {
        private Double budget;

        private Integer numShares;

        public MarketArgs(Object[] args) {
            this.budget = Double.valueOf((String) args[0]);
            this.numShares = args.length > 1 ? Integer.valueOf((String) args[1]) : null;
        }

        public Integer getNumShares() {
            return numShares;
        }

        public void setNumShares(Integer numShares) {
            this.numShares = numShares;
        }

        public Double getBudget() {
            return budget;
        }

        public void setBudget(Double budget) {
            this.budget = budget;
        }
    }
}
