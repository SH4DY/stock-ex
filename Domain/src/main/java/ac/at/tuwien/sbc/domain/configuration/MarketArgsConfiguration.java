package ac.at.tuwien.sbc.domain.configuration;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by dietl_ma on 29/05/15.
 */
@Component
public class MarketArgsConfiguration<T> {

    private HashMap<String, Object> args;

    @Autowired(required = false)
    private IMarketArgsMapper<T> mapper;

    @Value("${market}")
    public void setArgs(String[] marketArgs) {
        this.args = new HashMap<String, Object>();

        for (String marketArg : marketArgs) {

            String[] marketArgA = marketArg.split("\\#");
            //args.put(marketArgA[0], ArrayUtils.subarray(marketArgA, 1, marketArgA.length));
            T argObject = null;

            if (mapper != null)
                argObject = mapper.getObjectForArgs(ArrayUtils.subarray(marketArgA, 1, marketArgA.length));

            args.put(marketArgA[0], argObject);
        }
    }

    public T getArgsByMarket(String market) {
        return (T)args.get(market);
    }

    public ArrayList<String> getMarkets() {
            ArrayList<String> markets = new ArrayList<String>();
            markets.addAll(args.keySet());
            Collections.reverse(markets);

            return markets;
    }

    public HashMap<String, Object> getArgs() {
        return args;
    }

    public void setArgs(HashMap<String, Object> args) {
        this.args = args;
    }
}
