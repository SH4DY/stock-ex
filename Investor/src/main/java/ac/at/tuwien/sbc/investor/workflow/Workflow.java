package ac.at.tuwien.sbc.investor.workflow;

import ac.at.tuwien.sbc.domain.entry.InvestorDepotEntry;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import ac.at.tuwien.sbc.investor.gui.MainGUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dietl_ma on 26/03/15.
 */
@Service
public class Workflow implements ICoordinationServiceListener {

    @Value("${id}")
    private Integer investorId;

    @Value("${budget}")
    private Double budget;

    @Autowired
    private ICoordinationService coordinationService;

    @Autowired
    private IWorkFlowObserver observer;


    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(SpaceCoordinationService.class);

    @PostConstruct
    private void onPostConstruct() {

        //add listener to coordination service
        coordinationService.setListener(this);
        //init notifications
        initInvestorNotification();
        //init investor
        initInvestor();
    }


    private void initInvestorNotification() {
        coordinationService.registerInvestorNotification(investorId, new CoordinationListener<InvestorDepotEntry>() {
            @Override
            public void onResult(InvestorDepotEntry ide) {
                logger.info("Got InvestorDepotEntry notification: " + ide.getInvestorID() + "/" + ide.getBudget());

                if (observer != null) {
                    logger.info("Observer not null");
                    observer.onInvestorDepotEntryNotification(ide);
                }
            }
        });
    }
    /**
     * Get investor if exists and increase budget by new args
     */
    private void initInvestor() {

        coordinationService.getInvestor(investorId, new CoordinationListener<InvestorDepotEntry>() {
            @Override
            public void onResult(InvestorDepotEntry ide) {
                if (ide == null) {
                    //new entry
                    ide = new InvestorDepotEntry(investorId, budget, new HashMap<String, Integer>());
                }
                else {
                    logger.info("Got InvestorDepotEntry: " + ide.getInvestorID() + "/" + ide.getBudget());
                    //increase budget if already exists
                    ide.setBudget(ide.getBudget()+budget);
                }
                coordinationService.setInvestor(ide);
            }
        });
    }
}
