package ac.at.tuwien.sbc.investor.workflow;

import ac.at.tuwien.sbc.domain.entry.InvestorDepotEntry;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import org.mozartspaces.core.MzsCoreException;

/**
 * Created by dietl_ma on 26/03/15.
 */
public interface ICoordinationService {

    public void setListener(ICoordinationServiceListener listener);

    public void getInvestor(Integer id, CoordinationListener clistener);

    public void setInvestor(InvestorDepotEntry ide);
}
