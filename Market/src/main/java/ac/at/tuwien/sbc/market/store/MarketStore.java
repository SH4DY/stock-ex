package ac.at.tuwien.sbc.market.store;

import ac.at.tuwien.sbc.domain.entry.*;
import com.googlecode.cqengine.CQEngine;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.index.navigable.NavigableIndex;
import com.googlecode.cqengine.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by dietl_ma on 31/03/15.
 */
@Service
@Profile("amqp")
public class MarketStore {

    private IndexedCollection<InvestorDepotEntry> investorDepotEntries;
    private IndexedCollection<OrderEntry> orderEntries;
    private IndexedCollection<ShareEntry> shareEntries;
    private IndexedCollection<TransactionEntry> transactionEntries;

    private HashMap<Class, IndexedCollection> collectionMap = new HashMap<Class, IndexedCollection>();

    private static final Logger logger = LoggerFactory.getLogger(MarketStore.class);

    @PostConstruct
    private void onPostConstruct() {
        initCollections();
    }

    private void initCollections() {
        //initialize query-able collection
        investorDepotEntries = CQEngine.newInstance();
        orderEntries = CQEngine.newInstance();
        shareEntries = CQEngine.newInstance();
        transactionEntries = CQEngine.newInstance();

        //build indexes on attributes...
        investorDepotEntries.addIndex(NavigableIndex.onAttribute(CQAttributes.INVESTOR_INVESTOR_ID));
        orderEntries.addIndex(NavigableIndex.onAttribute(CQAttributes.ORDER_SHARE_ID));
        orderEntries.addIndex(NavigableIndex.onAttribute(CQAttributes.ORDER_TYPE));
        orderEntries.addIndex(NavigableIndex.onAttribute(CQAttributes.ORDER_STATUS));
        orderEntries.addIndex(NavigableIndex.onAttribute(CQAttributes.ORDER_LIMIT));
        shareEntries.addIndex(NavigableIndex.onAttribute(CQAttributes.SHARE_SHARE_ID));

        collectionMap.put(InvestorDepotEntry.class, investorDepotEntries);
        collectionMap.put(OrderEntry.class, orderEntries);
        collectionMap.put(ShareEntry.class, shareEntries);
        collectionMap.put(TransactionEntry.class, transactionEntries);

    }

    public ArrayList<SuperEntry> retrieve(Class clazz, Query query, Boolean shuffle, Integer numResults) {

        IndexedCollection col = collectionMap.get(clazz);

        if (shuffle != null && shuffle)
            Collections.shuffle((java.util.List<?>) col);

        if (numResults == null)
            numResults = 1;

        ArrayList<SuperEntry> result = new ArrayList<>();

        if (query == null) {
            for (Object object : col) {
                result.add((SuperEntry)object);
            }
            return result;
        }

        for (Object object : col.retrieve(query)) {
            if (result.size() >= numResults)
                break;

            result.add((SuperEntry)object);
        }
        return result;
    }

    public void delete(Class clazz, Object object) {

        IndexedCollection col = collectionMap.get(clazz);

        if (col.contains(object))
            col.remove(object);
    }

    public void add(Class clazz, Object object) {

        logger.info("WRITE:" + object.getClass());
        IndexedCollection col = collectionMap.get(clazz);
        col.add(object);
    }

}