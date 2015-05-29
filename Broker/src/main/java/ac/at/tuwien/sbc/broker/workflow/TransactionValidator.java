package ac.at.tuwien.sbc.broker.workflow;

import ac.at.tuwien.sbc.domain.entry.DepotEntry;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import ac.at.tuwien.sbc.domain.enums.DepotType;
import ac.at.tuwien.sbc.domain.enums.ShareType;

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
    public static HashMap<OrderEntry, Boolean> validate(OrderEntry sellOrder, OrderEntry buyOrder, DepotEntry seller, DepotEntry buyer, ShareEntry shareEntry, Double brokerProvision, Double fondFee) {

        HashMap<OrderEntry, Boolean> result = new HashMap<OrderEntry, Boolean>();
        Integer numSharesToTransact = Math.min(sellOrder.getNumTotal() - sellOrder.getNumCompleted(),
                buyOrder.getNumTotal() - buyOrder.getNumCompleted());

        result.put(sellOrder, true);
        result.put(buyOrder, true);

        if (seller != null &&
           (seller.getShareDepot().get(shareEntry.getShareID()) == null ||
            seller.getShareDepot().get(shareEntry.getShareID()) < numSharesToTransact ||
            (seller.getDepotType().equals(DepotType.FOND_MANAGER) && shareEntry.getShareType().equals(ShareType.FOND)))) {
            result.put(sellOrder, false);
        }


        Double charge = shareEntry.getShareType().equals(ShareType.FOND) ? brokerProvision+fondFee : brokerProvision;


        if ((buyer.getBudget() < (numSharesToTransact * shareEntry.getPrice()) * (1 + charge)) ||
            (buyer.getDepotType().equals(DepotType.FOND_MANAGER) && shareEntry.getShareType().equals(ShareType.FOND))) {
            result.put(buyOrder, false);
        }

        return result;
    }
}
