package ac.at.tuwien.sbc.investor.workflow;

import ac.at.tuwien.sbc.domain.entry.InvestorDepotEntry;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by dietl_ma on 27/03/15.
 */
@Service
@Profile("amqp")
public class AmqpCoordinationService implements ICoordinationService {


    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(AmqpCoordinationService.class);

    @PostConstruct
    public void onPostConstruct() {
        logger.info("I'M YOUR AmqpCoordinationService");
    }

    @Override
    public void setListener(ICoordinationServiceListener listener) {

    }

    @Override
    public void getInvestor(Integer investorId, CoordinationListener clistener) {

    }

    @Override
    public void registerInvestorNotification(Integer investorId, CoordinationListener clistener) {

    }

    @Override
    public void registerOrderNotification(Integer investorId, CoordinationListener clistener) {

    }

    @Override
    public void setInvestor(InvestorDepotEntry ide) {

    }

    @Override
    public void addOrder(OrderEntry oe) {

    }

    @Override
    public void getOrders(Integer investorId, CoordinationListener cListener) {

    }
}
