package ac.at.tuwien.sbc.domain.messaging;

import ac.at.tuwien.sbc.domain.entry.SuperEntry;
import com.googlecode.cqengine.query.Query;
import org.mozartspaces.core.MzsCoreException;

import java.util.ArrayList;

/**
 * Created by dietl_ma on 01/04/15.
 */
public class RPCMessageRequest {

    public static enum Method {GET_INVESTOR_DEPOT_ENTRY_BY_ID,
        TAKE_INVESTOR_DEPOT_ENTRY_BY_ID,
        DELETE_INVESTOR_DEPOT_ENTRY_BY_ID,
        WRITE_INVESTOR_DEPOT_ENTRY,
        TAKE_ORDER_BY_ORDER_ID,
        TAKE_ORDER_BY_PROPERTIES,
        READ_ORDER_BY_PROPERTIES,
        GET_ORDER_ENTRIES_BY_INVESTOR_ID,
        WRITE_ORDER_ENTRY,
        DELETE_ORDER_ENTRY_BY_ID,
        GET_SHARE_ENTRY_BY_ID,
        GET_SHARE_ENTRIES,
        DELETE_SHARE_ENTRY_BY_ID,
        WRITE_SHARE_ENTRY,
        WRITE_RELEASE_ENTRY,
        TAKE_RELEASE_ENTRY,
        WRITE_TRANSACTION_ENTRY,
        }

    private Method method;
    private Object[] args;
    private SuperEntry entry;
    private Boolean isRollBackAction;

    public RPCMessageRequest() {}

    public RPCMessageRequest(Method method,Object[] args) {
        this.method = method;
        this.args = args;
        this.entry = null;
        this.isRollBackAction = false;
    }

    public RPCMessageRequest(Method method,Object[] args, SuperEntry entry) {
        this.method = method;
        this.entry = entry;
        this.args = args;
        this.isRollBackAction = false;
    }

    public RPCMessageRequest(Method method,Object[] args, SuperEntry entry, Boolean isRollBackAction) {
        this.method = method;
        this.entry = entry;
        this.args = args;
        this.isRollBackAction = isRollBackAction;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method op) {
        this.method = op;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public SuperEntry getEntry() {
        return entry;
    }

    public void setEntry(SuperEntry entry) {
        this.entry = entry;
    }

    public Boolean getIsRollBackAction() {
        return isRollBackAction;
    }
}

