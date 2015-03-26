package ac.at.tuwien.sbc.domain.event;

import java.awt.event.ActionListener;

/**
 * Created by dietl_ma on 26/03/15.
 */
public interface CoordinationListener<T>  {

    public void onResult(T object);
}
