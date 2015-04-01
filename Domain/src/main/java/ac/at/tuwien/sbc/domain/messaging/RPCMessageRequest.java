package ac.at.tuwien.sbc.domain.messaging;

import ac.at.tuwien.sbc.domain.entry.SuperEntry;
import com.googlecode.cqengine.query.Query;

/**
 * Created by dietl_ma on 01/04/15.
 */
public class RPCMessageRequest {

    public static enum Operation {READ, WRITE, TAKE, DELETE}

    private Class clazz;
    private Query<SuperEntry> query;
    private SuperEntry object;
    private Operation op;
    private Integer numResults;
    private Boolean shuffle;

    public RPCMessageRequest() {}

    public RPCMessageRequest(Class clazz, Operation op,  Query<SuperEntry> query, SuperEntry object, Integer numResults, Boolean shuffle) {
        this.clazz = clazz;
        this.query = query;
        this.object = object;
        this.op = op;
        this.numResults = numResults;
        this.shuffle = shuffle;
    }



    public Integer getNumResults() {
        return numResults;
    }

    public void setNumResults(Integer numResults) {
        this.numResults = numResults;
    }

    public Query<SuperEntry> getQuery() {
        return query;
    }

    public void setQuery(Query<SuperEntry> query) {
        this.query = query;
    }

    public Class getClazz() {
        return clazz;

    }
    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public SuperEntry getObject() {
        return object;
    }

    public void setObject(SuperEntry object) {
        this.object = object;
    }

    public Boolean getShuffle() {
        return shuffle;
    }

    public void setShuffle(Boolean shuffle) {
        this.shuffle = shuffle;
    }

    public Operation getOp() {
        return op;
    }

    public void setOp(Operation op) {
        this.op = op;
    }

}

