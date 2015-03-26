package ac.at.tuwien.sbc.investor.workflow;

import ac.at.tuwien.sbc.domain.entry.InvestorDepotEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by dietl_ma on 26/03/15.
 */
@Service
public class Workflow implements ICoordinationServiceListener {

    @Value("${id}")
    private Integer id;

    @Value("${budget}")
    private Double budget;

    @Autowired
    private ICoordinationService coordinationService;

    @PostConstruct
    private void onPostConstruct() {

        //add listener to coordination service
        coordinationService.setListener(this);
        //init investor
        coordinationService.initInvestor(id, budget);
    }

    private void initInvestor() {

    }

    @Override
    public void onInitInvestor(InvestorDepotEntry ide) {

    }
}
