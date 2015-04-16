package ac.at.tuwien.sbc.marketagent.workflow.space;

import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import ac.at.tuwien.sbc.domain.enums.OrderStatus;
import ac.at.tuwien.sbc.domain.enums.OrderType;
import ac.at.tuwien.sbc.marketagent.workflow.ICoordinationService;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static org.mozartspaces.capi3.Matchmakers.and;
import static org.mozartspaces.capi3.Matchmakers.or;

// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 30/03/15.
 */
@Service
@Profile("space")
public class SpaceCoordinationService implements ICoordinationService {

    /** The core. */
    @Autowired
    MzsCore core;

    /** The capi. */
    @Autowired
    Capi capi;

    /** The share container. */
    @Autowired
    @Qualifier("shareContainer")
    ContainerReference shareContainer;

    /** The order container. */
    @Autowired
    @Qualifier("orderContainer")
    ContainerReference orderContainer;

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(SpaceCoordinationService.class);

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.marketagent.workflow.ICoordinationService#getShares()
     */
    @Override
    public ArrayList<ShareEntry> getShares() {

        ArrayList<ShareEntry> entries = new ArrayList<ShareEntry>();
        try {
            entries = capi.read(shareContainer, FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL), MzsConstants.RequestTimeout.TRY_ONCE, null);
        }
        catch (MzsCoreException e) {}
        return entries;
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.marketagent.workflow.ICoordinationService#setShareEntry(ac.at.tuwien.sbc.domain.entry.ShareEntry)
     */
    @Override
    public void setShareEntry(ShareEntry se) {
        try {
            ArrayList<ShareEntry> entries = capi.take(shareContainer, KeyCoordinator.newSelector(se.getShareID()), MzsConstants.RequestTimeout.TRY_ONCE, null);

            if (entries != null && !entries.isEmpty()) {
                Entry entryToUpdate = new Entry(se, KeyCoordinator.newCoordinationData(se.getShareID()));
                capi.write(shareContainer, MzsConstants.RequestTimeout.ZERO, null, entryToUpdate);
            }
        }
        catch (MzsCoreException e) {}
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.marketagent.workflow.ICoordinationService#getOrdersByProperties(java.lang.String, ac.at.tuwien.sbc.domain.enums.OrderType)
     */
    @Override
    public ArrayList<OrderEntry> getOrdersByProperties(String shareId, OrderType type) {

        Matchmaker mShareId = Property.forName("shareID").equalTo(shareId);
        Matchmaker mType = Property.forName("type").equalTo(type);
        Matchmaker mStatus1 = Property.forName("status").equalTo(OrderStatus.OPEN);
        Matchmaker mStatus2 = Property.forName("status").equalTo(OrderStatus.PARTIAL);

        Query q = new Query().filter(and(mShareId, mType, or(mStatus1, mStatus2))).cnt(MzsConstants.Selecting.COUNT_ALL);
        ArrayList<OrderEntry> entries = null;
        try {

            ArrayList<Selector> selectors = new ArrayList<Selector>();
            selectors.add(QueryCoordinator.newSelector(q, MzsConstants.Selecting.COUNT_ALL));

            entries = capi.read(orderContainer, selectors, MzsConstants.RequestTimeout.TRY_ONCE, null);
        } catch (MzsCoreException e) {logger.info("Try to get order by properties FAILED:" + e.getMessage());}

        return entries;
    }
}
