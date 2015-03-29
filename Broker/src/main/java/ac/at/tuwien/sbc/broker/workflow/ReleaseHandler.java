package ac.at.tuwien.sbc.broker.workflow;

import ac.at.tuwien.sbc.domain.entry.ReleaseEntry;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by dietl_ma on 28/03/15.
 */
public class ReleaseHandler implements Runnable {

    private ICoordinationService coordinationService;
    private IReleaseHandlerObserver observer;


    public ReleaseHandler(ICoordinationService coordinationService, IReleaseHandlerObserver observer) {
        this.coordinationService = coordinationService;
        this.observer = observer;
    }

    @Override
    public void run() {


    }
}
