package ac.at.tuwien.sbc.investor.workflow;

/**
 * Created by dietl_ma on 26/03/15.
 */
public interface ICoordinationService {

    public void setListener(ICoordinationServiceListener listener);

    public void initInvestor(Integer id, Double budget);
}
