package ac.at.tuwien.sbc.marketagent.workflow.space;

import ac.at.tuwien.sbc.domain.configuration.CommonSpaceConfiguration;
import ac.at.tuwien.sbc.domain.entry.DepotEntry;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import ac.at.tuwien.sbc.domain.enums.OrderStatus;
import ac.at.tuwien.sbc.domain.enums.OrderType;
import ac.at.tuwien.sbc.domain.event.CoordinationListener;
import ac.at.tuwien.sbc.domain.util.SpaceUtils;
import ac.at.tuwien.sbc.marketagent.workflow.ICoordinationService;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

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

    /** The depot container. */
    @Autowired
    @Qualifier(CommonSpaceConfiguration.DEPOT_CONTAINER_MAP)
    HashMap<String, ContainerReference> depotContainer;

    /** The order container. */
    @Autowired
    @Qualifier(CommonSpaceConfiguration.ORDER_CONTAINER_MAP)
    HashMap<String, ContainerReference> orderContainer;

    /** The share container. */
    @Autowired
    @Qualifier(CommonSpaceConfiguration.SHARE_CONTAINER_MAP)
    HashMap<String, ContainerReference> shareContainer;

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(SpaceCoordinationService.class);

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.marketagent.workflow.ICoordinationService#getShares()
     */
    @Override
    public ArrayList<ShareEntry> getShares(String market) {

        ArrayList<ShareEntry> entries = new ArrayList<ShareEntry>();
        try {
            entries = capi.read(shareContainer.get(market), FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL), MzsConstants.RequestTimeout.TRY_ONCE, null);
        }
        catch (MzsCoreException e) {}
        return entries;
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.marketagent.workflow.ICoordinationService#setShareEntry(ac.at.tuwien.sbc.domain.entry.ShareEntry)
     */
    @Override
    public void setShareEntry(ShareEntry se, String market) {
        try {
            ArrayList<ShareEntry> entries = capi.take(shareContainer.get(market), KeyCoordinator.newSelector(se.getShareID()), MzsConstants.RequestTimeout.TRY_ONCE, null);

            if (entries != null && !entries.isEmpty()) {
                Entry entryToUpdate = new Entry(se, KeyCoordinator.newCoordinationData(se.getShareID()));
                capi.write(shareContainer.get(market), MzsConstants.RequestTimeout.ZERO, null, entryToUpdate);
            }
        }
        catch (MzsCoreException e) {}
    }


    @Override
    public ShareEntry getShareEntry(String shareId, String market) {

        ShareEntry se = null;
        ArrayList<ShareEntry> entries = null;
        try {
            entries = capi.read(shareContainer.get(market), KeyCoordinator.newSelector(shareId), MzsConstants.RequestTimeout.ZERO, null);
        }
        catch (MzsCoreException e) {
            logger.info("Share not found for: " + shareId);
        }
        if (entries != null && !entries.isEmpty())
            se = entries.get(0);

        return se;
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.marketagent.workflow.ICoordinationService#getOrdersByProperties(java.lang.String, ac.at.tuwien.sbc.domain.enums.OrderType)
     */
    @Override
    public ArrayList<OrderEntry> getOrdersByProperties(String shareId, OrderType type, String market) {

        Matchmaker mShareId = Property.forName("shareID").equalTo(shareId);
        Matchmaker mType = Property.forName("type").equalTo(type);
        Matchmaker mStatus1 = Property.forName("status").equalTo(OrderStatus.OPEN);
        Matchmaker mStatus2 = Property.forName("status").equalTo(OrderStatus.PARTIAL);

        Query q = new Query().filter(and(mShareId, mType, or(mStatus1, mStatus2))).cnt(MzsConstants.Selecting.COUNT_ALL);
        ArrayList<OrderEntry> entries = null;
        try {

            ArrayList<Selector> selectors = new ArrayList<Selector>();
            selectors.add(QueryCoordinator.newSelector(q, MzsConstants.Selecting.COUNT_ALL));

            entries = capi.read(orderContainer.get(market), selectors, MzsConstants.RequestTimeout.TRY_ONCE, null);
        } catch (MzsCoreException e) {logger.info("Try to get order by properties FAILED:" + e.getMessage());}

        return entries;
    }

    @Override
    public DepotEntry getDepot(String depotId, String market) {
        ArrayList<DepotEntry> entries = null;
        DepotEntry entry = null;
        try {
            entries = capi.read(depotContainer.get(market), KeyCoordinator.newSelector(depotId.toString()), MzsConstants.RequestTimeout.ZERO, null);
        } catch (MzsCoreException e) {
            logger.info("Depot not found for: " + depotId);
        }

        if (entries != null && !entries.isEmpty())
            entry = entries.get(0);

        return  entry;

    }



    @Override
    public void addMarket(String market) {

        try {
            URI uri = new URI(CommonSpaceConfiguration.SPACE_URI_PREFIX + market);

            if (!depotContainer.containsKey(market))
                depotContainer.put(market, SpaceUtils.getNamedContainer(uri, CommonSpaceConfiguration.DEPOT_CONTAINER, capi));
            if (!orderContainer.containsKey(market))
                orderContainer.put(market, SpaceUtils.getNamedContainer(uri, CommonSpaceConfiguration.ORDER_CONTAINER, capi));
            if (!shareContainer.containsKey(market))
                shareContainer.put(market, SpaceUtils.getNamedContainer(uri, CommonSpaceConfiguration.SHARE_CONTAINER, capi));

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

    }
}
