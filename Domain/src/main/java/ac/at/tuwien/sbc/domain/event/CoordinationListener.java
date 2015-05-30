package ac.at.tuwien.sbc.domain.event;

// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 26/03/15.
 *
 * @param <T> the generic type
 * @see CoordinationEvent
 */
public interface CoordinationListener<T>  {

    /**
     * On result.
     *
     * @param object the object
     */
    public void onResult(T object);
}
