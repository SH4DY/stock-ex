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

// TODO: Auto-generated Javadoc
/**
 * This class acts as the database of all the market environment.
 * Created by dietl_ma on 31/03/15.
 */
@Service
@Profile("amqp")
public class MarketStore {

    /** The investor depot entries. */
    private IndexedCollection<DepotEntry> depotEntries;
    
    /** The order entries. */
    private IndexedCollection<OrderEntry> orderEntries;
    
    /** The share entries. */
    private IndexedCollection<ShareEntry> shareEntries;
    
    /** The transaction entries. */
    private IndexedCollection<TransactionEntry> transactionEntries;
    
    /** The release entries. */
    private IndexedCollection<ReleaseEntry> releaseEntries;

    /** The collection map. */
    private HashMap<Class, IndexedCollection> collectionMap = new HashMap<Class, IndexedCollection>();

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(MarketStore.class);

    /**
     * On post construct.
     */
    @PostConstruct
    private void onPostConstruct() {
        initCollections();
    }

    /**
     * Inits the collections.
     */
    private void initCollections() {
        //initialize query-able collection
        depotEntries = CQEngine.newInstance();
        orderEntries = CQEngine.newInstance();
        shareEntries = CQEngine.newInstance();
        transactionEntries = CQEngine.newInstance();
        releaseEntries = CQEngine.newInstance();

        //build indexes on attributes...
        depotEntries.addIndex(NavigableIndex.onAttribute(CQAttributes.DEPOT_ID));
        orderEntries.addIndex(NavigableIndex.onAttribute(CQAttributes.ORDER_SHARE_ID));
        orderEntries.addIndex(NavigableIndex.onAttribute(CQAttributes.ORDER_TYPE));
        orderEntries.addIndex(NavigableIndex.onAttribute(CQAttributes.ORDER_STATUS));
        orderEntries.addIndex(NavigableIndex.onAttribute(CQAttributes.ORDER_LIMIT));
        shareEntries.addIndex(NavigableIndex.onAttribute(CQAttributes.SHARE_SHARE_ID));

        collectionMap.put(DepotEntry.class, depotEntries);
        collectionMap.put(OrderEntry.class, orderEntries);
        collectionMap.put(ShareEntry.class, shareEntries);
        collectionMap.put(TransactionEntry.class, transactionEntries);
        collectionMap.put(ReleaseEntry.class, releaseEntries);
    }

    /**
     * Retrieve.
     *
     * @param clazz the clazz
     * @param query the query
     * @param shuffle the shuffle
     * @param numResults the num results
     * @return the array list
     */
    public ArrayList<SuperEntry> retrieve(Class clazz, Query query, Boolean shuffle, Integer numResults) {

        IndexedCollection col = collectionMap.get(clazz);

        if (shuffle != null && shuffle) {
            ArrayList<Object> listToShuffle = new ArrayList<Object>();

            for (Object object : col)
                listToShuffle.add(object);

            Collections.shuffle(listToShuffle);

            col.clear();
            col.addAll(listToShuffle);
        }

        if (numResults == null)
            numResults = 1;

        ArrayList<SuperEntry> result = new ArrayList<>();

        if (query == null) {
            for (Object object : col) {
                if (result.size() >= numResults)
                    break;

                result.add((SuperEntry) object);
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

    /**
     * Delete.
     *
     * @param clazz the clazz
     * @param object the object
     */
    public void delete(Class clazz, Object object) {

        IndexedCollection col = collectionMap.get(clazz);

        if (col.contains(object))
            col.remove(object);
    }

    /**
     * Adds the.
     *
     * @param clazz the clazz
     * @param object the object
     */
    public void add(Class clazz, Object object) {

        logger.info("WRITE:" + object.getClass());
        IndexedCollection col = collectionMap.get(clazz);
        col.add(object);
    }

}
