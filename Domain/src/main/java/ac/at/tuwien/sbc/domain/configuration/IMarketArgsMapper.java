package ac.at.tuwien.sbc.domain.configuration;

/**
 * Created by dietl_ma on 29/05/15.
 */
public interface IMarketArgsMapper<T> {

    public T getObjectForArgs(Object[] args);
}
