package ac.at.tuwien.sbc.broker.workflow;

import ac.at.tuwien.sbc.domain.entry.ReleaseEntry;

/**
 * Created by dietl_ma on 28/03/15.
 */
public interface IReleaseHandlerObserver {

    public void onRelease(ReleaseEntry re);
}
