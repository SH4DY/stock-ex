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
        DELETE_INVESTOR_DEPOT_ENTRY_BY_ID,
        WRITE_INVESTOR_DEPOT_ENTRY}

    private Method method;
    private Object[] args;
    private SuperEntry entry;

    public RPCMessageRequest() {}

    public RPCMessageRequest(Method method,Object[] args) {
        this.method = method;
        this.args = args;
        this.entry = null;
    }

    public RPCMessageRequest(Method method,Object[] args, SuperEntry entry) {
        this.method = method;
        this.entry = entry;
        this.args = args;
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
}

