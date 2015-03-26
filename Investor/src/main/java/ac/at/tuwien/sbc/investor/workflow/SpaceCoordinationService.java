package ac.at.tuwien.sbc.investor.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by dietl_ma on 26/03/15.
 */
@Service
@Profile("space")
public class SpaceCoordinationService implements ICoordinationService {

    private ICoordinationServiceListener listener;

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(SpaceCoordinationService.class);

    @Override
    public void setListener(ICoordinationServiceListener l) {
        listener = l;
    }

    @Override
    public void initInvestor(Integer id, Double budget) {
        logger.info("Try to init investor with arguments:" + String.valueOf(id) + ", " + String.valueOf(budget));

    }

    @PostConstruct
    public void onPostConstruct() {

    }
}
