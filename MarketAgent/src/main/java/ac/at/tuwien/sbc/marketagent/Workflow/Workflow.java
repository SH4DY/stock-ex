package ac.at.tuwien.sbc.marketagent.workflow;

import ac.at.tuwien.sbc.domain.configuration.MarketArgsConfiguration;
import ac.at.tuwien.sbc.domain.entry.DepotEntry;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import ac.at.tuwien.sbc.domain.enums.OrderType;
import ac.at.tuwien.sbc.domain.service.IMarketDirectoryService;
import ac.at.tuwien.sbc.marketagent.EntryMarketCorrelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 30/03/15.
 */
@Service
public class Workflow {

    /** The coordination service. */
    @Autowired
    private ICoordinationService coordinationService;

    @Autowired
    private MarketArgsConfiguration marketArgs;

    @Autowired
    private IMarketDirectoryService marketDirectoryService;

    private List<String> allMarkets = new ArrayList<>();
    /** The counter. */
    private Integer counter = 0;
    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(Workflow.class);

    @PostConstruct
    public void onPostConstruct() {

        //init all markets
        if (marketDirectoryService != null) {
            for (String market : marketDirectoryService.getMarkets()) {

                coordinationService.addMarket(market);
                allMarkets.add(market);
            }
        }

    }

    /**
     * Do manipulation.
     */
    @Scheduled(fixedDelay = 2000)
    public void doManipulation() {

        HashMap<String, EntryMarketCorrelation<ShareEntry>> shareMap = new HashMap<>();
        //get all shares for each market
        ArrayList<ShareEntry> shares = new ArrayList<>();
        for (String market : (ArrayList<String>)marketArgs.getMarkets()) {

            ArrayList<ShareEntry> sharesForMarket = coordinationService.getShares(market);

            if (sharesForMarket.isEmpty()) continue;

            shares.addAll(sharesForMarket);

            for (ShareEntry se : sharesForMarket)
                shareMap.put(se.getShareID(), new EntryMarketCorrelation<ShareEntry>(se, market));
        }

        if (shares.isEmpty()) return;

        for (ShareEntry se : shares) {
            switch (se.getShareType()) {
                case SHARE:
                    manipulateShare(se, shareMap);
                    break;
                case FOND:
                    manipulateFonds(se, shareMap);
            }
        }
        counter++;
    }

    /**
     * Manipulate share
     * @param se
     */
    public void manipulateShare(ShareEntry se, HashMap<String, EntryMarketCorrelation<ShareEntry>> shareMap) {

        EntryMarketCorrelation shareEntryMarketC =  shareMap.get(se.getShareID());

        ArrayList<OrderEntry> sellOrders = coordinationService.getOrdersByProperties(se.getShareID(), OrderType.SELL, shareEntryMarketC.getMarket());
        ArrayList<OrderEntry> buyOrders = coordinationService.getOrdersByProperties(se.getShareID(), OrderType.BUY, shareEntryMarketC.getMarket());

        if (sellOrders == null || buyOrders == null) return;

        Double numSellOrders = 0.0;
        for (OrderEntry oe : sellOrders)
            numSellOrders += oe.getNumTotal()-oe.getNumCompleted();

        Double numBuyOrders = 0.0;
        for (OrderEntry oe : buyOrders)
            numBuyOrders += oe.getNumTotal()-oe.getNumCompleted();

        Double currentPrice = se.getPrice();

        Double newPrice = Math.max(1, currentPrice * (1.0 + ((numBuyOrders-numSellOrders)/(Math.max(1,numBuyOrders+numSellOrders)) * 1/16)));

        if (counter % 3 == 0) {
            newPrice = Math.max(1, newPrice * (0.97 + Math.random()*0.06));
        }

        //logger.info("Manipulation:" + numBuyOrders + "/" + numSellOrders);
        logger.info("Share Manipulation:" + se.getShareID() + "/" + currentPrice + "->" + newPrice);

        se.setPrice(newPrice);
        coordinationService.setShareEntry(se, shareEntryMarketC.getMarket());
    }

    /**
     * Manipulate fond
     * @param se
     * @param shareMap
     */
    public void manipulateFonds(ShareEntry se, HashMap<String, EntryMarketCorrelation<ShareEntry>> shareMap) {

        EntryMarketCorrelation shareEntryMarketC =  shareMap.get(se.getShareID());

        //get depots from markets end store shares in list
        ArrayList<String> shareIds = new ArrayList<String>();
        HashMap<String, Integer> shareDepotMap = new HashMap<>();

        Double sumBudget = 0.0;

        //check if we should use given market or markets from the directory
        List<String> markets = allMarkets.isEmpty() ? (ArrayList<String>)marketArgs.getMarkets() : allMarkets;

        for (String market : markets) {
            DepotEntry de = coordinationService.getDepot(se.getShareID(), market);

            if (de != null) {
                shareIds.addAll(de.getShareDepot().keySet());
                shareDepotMap.putAll(de.getShareDepot());

                //add shares to map
                for (String shareId : de.getShareDepot().keySet()) {
                    extendShareMap(shareMap, shareId, market);
                }
                sumBudget += de.getBudget();
            }
        }

        //calculate sum of share assets
        Double sumShareAsset = 0.0;
        for (String shareId : shareIds) {
            if (shareMap.containsKey(shareId)) {
                Integer numShares = shareDepotMap.get(shareId);
                sumShareAsset += numShares * shareMap.get(shareId).getEntry().getPrice();
            }
        }

        Double newPrice = (sumBudget + sumShareAsset) / se.getNumShares();
        logger.info("Fond Manipulation:" + se.getShareID() + "/" + se.getPrice() + "->" + newPrice);

        se.setPrice(newPrice);
        coordinationService.setShareEntry(se, shareEntryMarketC.getMarket());
    }

    /**
     * Extends share map for new share in market
     * @param shareMap
     * @param shareId
     */
    private void extendShareMap(HashMap<String, EntryMarketCorrelation<ShareEntry>> shareMap, String shareId, String market) {

        if (!shareMap.containsKey(shareId)) {
            ShareEntry newShare = coordinationService.getShareEntry(shareId, market);
            if (newShare != null) {
                shareMap.put(shareId, new EntryMarketCorrelation<ShareEntry>(newShare, market));
                logger.info("GOT FOREIGN SHARE ENTRY: " + newShare.getShareID());
            }
        }
    }
}
