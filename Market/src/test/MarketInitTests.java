import ac.at.tuwien.sbc.domain.configuration.CommonSpaceConfiguration;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.enums.OrderStatus;
import ac.at.tuwien.sbc.domain.enums.OrderType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Run this test class before running the Market GUI to see the GUI working
 * Created by shady on 26/03/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CommonSpaceConfiguration.class)
@ActiveProfiles("space")
public class MarketInitTests {

    @Autowired
    Capi capi;

    @Autowired
    MzsCore core;

    @Autowired
    @Qualifier("orderContainer")
    ContainerReference orderContainer;

    private static final Logger logger = LoggerFactory.getLogger(MarketInitTests.class);

    @Before
    public void writeOrders(){
        //DELETE Existing order entries
        int deletedEntries = 0;
        try {
            deletedEntries = capi.delete(orderContainer, FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL), MzsConstants.RequestTimeout.INFINITE, null);
        } catch (MzsCoreException e) {
            logger.error("Exception while trying to delete all entries from orderContainer");
        }

        //Write new orders to orderContainer
        OrderEntry order1 = new OrderEntry(UUID.randomUUID(),110, "GOOG", OrderType.BUY, 150.00, 10,10, OrderStatus.OPEN);
        OrderEntry order2 = new OrderEntry(UUID.randomUUID(),249, "MSFT", OrderType.BUY, 150.00, 10,10, OrderStatus.OPEN);
        OrderEntry order3 = new OrderEntry(UUID.randomUUID(),330, "YAHO", OrderType.BUY, 150.00, 10,10, OrderStatus.OPEN);

        Entry[] entries = {new Entry(order1, FifoCoordinator.newCoordinationData()),
                new Entry(order2, FifoCoordinator.newCoordinationData()),
                new Entry(order3, FifoCoordinator.newCoordinationData())};

        try {
            capi.write(orderContainer, MzsConstants.RequestTimeout.TRY_ONCE,null, entries);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void numberOfOrders() throws Exception {
        ArrayList<Entry> resultEntries = capi.read(orderContainer, FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL),10,null);
        Assert.assertTrue(resultEntries.size() == 3);
    }

    @Test
    public void orderDetails(){
        ArrayList<OrderEntry> resultEntries = null;
        try {
            resultEntries = capi.read(orderContainer, FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL),10,null);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
//        OrderEntry oe1 = ((OrderEntry)(resultEntries.get(0).getValue()));
        OrderEntry oe1 = resultEntries.get(0);
        Assert.assertTrue(oe1.getShareID().equals("GOOG"));
        Assert.assertTrue(oe1.getStatus().equals(OrderStatus.OPEN));

//        OrderEntry oe2 = ((OrderEntry)(resultEntries.get(1).getValue()));
        OrderEntry oe2 = resultEntries.get(1);
        Assert.assertTrue(oe2.getShareID().equals("MSFT"));
        Assert.assertTrue(oe2.getStatus().equals(OrderStatus.OPEN));

//        OrderEntry oe3 = ((OrderEntry)(resultEntries.get(2).getValue()));
        OrderEntry oe3 = resultEntries.get(2);
        Assert.assertTrue(oe3.getShareID().equals("YAHO"));
        Assert.assertTrue(oe3.getStatus().equals(OrderStatus.OPEN));
    }




}
