package ac.at.tuwien.sbc.marketagent.workflow;

import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import ac.at.tuwien.sbc.domain.enums.OrderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Created by dietl_ma on 30/03/15.
 */
@Service
public class Workflow {

    @Autowired
    private ICoordinationService coordinationService;

    private Integer counter = 0;
    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(Workflow.class);

    @Scheduled(fixedDelay = 2000)
    public void doManipulation() {

        ArrayList<ShareEntry> shares = coordinationService.getShares();

        for (ShareEntry se : shares) {

            ArrayList<OrderEntry> sellOrders = coordinationService.getOrdersByProperties(se.getShareID(), OrderType.SELL);
            ArrayList<OrderEntry> buyOrders = coordinationService.getOrdersByProperties(se.getShareID(), OrderType.BUY);

            if (sellOrders == null || buyOrders == null) continue;

            Double numSellOrders = 0.0;
            for (OrderEntry oe : sellOrders) numSellOrders += oe.getNumTotal()-oe.getNumCompleted();

            Double numBuyOrders = 0.0;
            for (OrderEntry oe : buyOrders) numBuyOrders += oe.getNumTotal()-oe.getNumCompleted();

            Double currentPrice = se.getPrice();

            Double newPrice = Math.max(1, currentPrice * (1.0 + ((numBuyOrders-numSellOrders)/(Math.max(1,numBuyOrders+numSellOrders)) * 1/16)));

            if (counter % 3 == 0) {
                newPrice = Math.max(1, newPrice * (0.97 + Math.random()*0.06));
            }

            //logger.info("Manipulation:" + numBuyOrders + "/" + numSellOrders);
            logger.info("Manipulation:" + se.getShareID() + "/" + currentPrice + "->" + newPrice);

            se.setPrice(newPrice);
            coordinationService.setShareEntry(se);
        }

        counter++;
    }
}
