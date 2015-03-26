package ac.at.tuwien.sbc.investor.workflow;

import ac.at.tuwien.sbc.domain.configuration.CommonSpaceConfiguration;
import ac.at.tuwien.sbc.domain.entry.InvestorDepotEntry;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import org.mozartspaces.capi3.KeyCoordinator;
import org.mozartspaces.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

/**
 * Created by dietl_ma on 26/03/15.
 */
@Service
@Profile("space")
public class SpaceCoordinationService implements ICoordinationService {

    @Autowired
    Capi capi;

    @Autowired
    @Qualifier("investorDepotContainer")
    ContainerReference investorDepotContainer;

    private ICoordinationServiceListener listener;

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(SpaceCoordinationService.class);

    @Override
    public void setListener(ICoordinationServiceListener l) {
        listener = l;
    }

    @Override
    public void getInvestor(Integer id, CoordinationListener cListener) {
        logger.info("Try to read investor with arguments: " + String.valueOf(id));

        ArrayList<InvestorDepotEntry> entries = null;
        try {
            entries = capi.read(investorDepotContainer, KeyCoordinator.newSelector(id.toString()), MzsConstants.RequestTimeout.ZERO, null);
        } catch (MzsCoreException e) {
            logger.info("Investor depot not found for: " + id);
        }

        if (entries != null && !entries.isEmpty())
            //listener.onGetInvestor(entries.get(0));
            cListener.onResult(entries.get(0));
        else {
            cListener.onResult(null);
        }

    }

    @Override
    public void setInvestor(InvestorDepotEntry ide) {

        logger.info("Try to write InvestorDepotEntry: " + ide.getBudget().toString());
        TransactionReference tx = null;
        try {
            tx = capi.createTransaction(1000, investorDepotContainer.getSpace());

            try {
                capi.take(investorDepotContainer, KeyCoordinator.newSelector(ide.getInvestorID().toString()), MzsConstants.RequestTimeout.TRY_ONCE, tx);
            }
            catch (MzsCoreException e) {}

            Entry entryToUpdate = new Entry(ide, KeyCoordinator.newCoordinationData(ide.getInvestorID().toString()));
            capi.write(investorDepotContainer, MzsConstants.RequestTimeout.ZERO, tx, entryToUpdate);

            capi.commitTransaction(tx);
        }
        catch (MzsCoreException e) {
            try {
                capi.rollbackTransaction(tx);
            } catch (MzsCoreException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            logger.info("Something went wrong writing a InvestorDepotEntry");
        }
    }

    @PostConstruct
    public void onPostConstruct() {

    }
}
