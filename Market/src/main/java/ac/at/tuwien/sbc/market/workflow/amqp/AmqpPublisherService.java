package ac.at.tuwien.sbc.market.workflow.amqp;

import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.TransactionEntry;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import ac.at.tuwien.sbc.market.workflow.IMarketPublisherService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Created by dietl_ma on 01/04/15.
 */
@Service
@Profile("amqp")
public class AmqpPublisherService implements IMarketPublisherService {
    @Override
    public void registerOrderObserver(CoordinationListener<OrderEntry> coordinationListener) {

    }

    @Override
    public void getOrders(CoordinationListener listener) {

    }

    @Override
    public void registerTransactionObserver(CoordinationListener<TransactionEntry> coordinationListener) {

    }

    @Override
    public void getTransactions(CoordinationListener listener) {

    }
}
