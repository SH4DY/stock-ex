package ac.at.tuwien.sbc.broker.workflow;

import ac.at.tuwien.sbc.domain.entry.InvestorDepotEntry;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import ac.at.tuwien.sbc.domain.enums.OrderStatus;
import ac.at.tuwien.sbc.domain.exception.CoordinationServiceException;

import java.util.HashMap;

// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 30/03/15.
 */
public class TransactionValidator {

    /**
     * Validate.
     *
     * @param sellOrder the sell order
     * @param buyOrder the buy order
     * @param seller the seller
     * @param buyer the buyer
     * @param shareEntry the share entry
     * @param brokerProvision the broker provision
     * @return the hash map
     */
    public static HashMap<OrderEntry, Boolean> validate(OrderEntry sellOrder, OrderEntry buyOrder, InvestorDepotEntry seller, InvestorDepotEntry buyer, ShareEntry shareEntry, Double brokerProvision) {

        HashMap<OrderEntry, Boolean> result = new HashMap<OrderEntry, Boolean>();
        Integer numSharesToTransact = Math.min(sellOrder.getNumTotal() - sellOrder.getNumCompleted(),
                buyOrder.getNumTotal() - buyOrder.getNumCompleted());

        result.put(sellOrder, true);
        result.put(buyOrder, true);

        if (seller != null &&
           (seller.getShareDepot().get(shareEntry.getShareID()) == null ||
            seller.getShareDepot().get(shareEntry.getShareID()) < numSharesToTransact)) {
            result.put(sellOrder, false);
        }

        if (buyer.getBudget() < (numSharesToTransact * shareEntry.getPrice()) * (1 + brokerProvision)) {
            result.put(buyOrder, false);
        }

        return result;
    }
}
