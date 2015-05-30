package ac.at.tuwien.sbc.company.configuration;

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
        private Integer numShares;

        private Double initPrice;

        public MarketArgs(Object[] args) {
            this.numShares = Integer.valueOf((String) args[0]);
            this.initPrice = Double.valueOf((String) args[1]);
        }

        public Integer getNumShares() {
            return numShares;
        }

        public void setNumShares(Integer numShares) {
            this.numShares = numShares;
        }

        public Double getInitPrice() {
            return initPrice;
        }

        public void setInitPrice(Double initPrice) {
            this.initPrice = initPrice;
        }
    }
}

