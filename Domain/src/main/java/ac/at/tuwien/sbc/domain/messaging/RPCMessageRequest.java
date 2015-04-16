package ac.at.tuwien.sbc.domain.messaging;

import ac.at.tuwien.sbc.domain.entry.SuperEntry;
import com.googlecode.cqengine.query.Query;
import org.mozartspaces.core.MzsCoreException;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 01/04/15.
 */
public class RPCMessageRequest {

    /**
     * The Enum Method.
     */
    public static enum Method {/** The get investor depot entry by id. */
GET_INVESTOR_DEPOT_ENTRY_BY_ID,
        
        /** The take investor depot entry by id. */
        TAKE_INVESTOR_DEPOT_ENTRY_BY_ID,
        
        /** The delete investor depot entry by id. */
        DELETE_INVESTOR_DEPOT_ENTRY_BY_ID,
        
        /** The write investor depot entry. */
        WRITE_INVESTOR_DEPOT_ENTRY,
        
        /** The take order by order id. */
        TAKE_ORDER_BY_ORDER_ID,
        
        /** The take order by properties. */
        TAKE_ORDER_BY_PROPERTIES,
        
        /** The read order by properties. */
        READ_ORDER_BY_PROPERTIES,
        
        /** The get order entries by investor id. */
        GET_ORDER_ENTRIES_BY_INVESTOR_ID,
        
        /** The write order entry. */
        WRITE_ORDER_ENTRY,
        
        /** The delete order entry by id. */
        DELETE_ORDER_ENTRY_BY_ID,
        
        /** The get share entry by id. */
        GET_SHARE_ENTRY_BY_ID,
        
        /** The get share entries. */
        GET_SHARE_ENTRIES,
        
        /** The delete share entry by id. */
        DELETE_SHARE_ENTRY_BY_ID,
        
        /** The write share entry. */
        WRITE_SHARE_ENTRY,
        
        /** The write release entry. */
        WRITE_RELEASE_ENTRY,
        
        /** The take release entry. */
        TAKE_RELEASE_ENTRY,
        
        /** The write transaction entry. */
        WRITE_TRANSACTION_ENTRY,
        
        /** The get transaction entries. */
        GET_TRANSACTION_ENTRIES,
        
        /** The get order entries. */
        GET_ORDER_ENTRIES
        }

    /** The method. */
    private Method method;
    
    /** The args. */
    private Object[] args;
    
    /** The entry. */
    private SuperEntry entry;
    
    /** The is roll back action. */
    private Boolean isRollBackAction;

    /**
     * Instantiates a new RPC message request.
     */
    public RPCMessageRequest() {}

    /**
     * Instantiates a new RPC message request.
     *
     * @param method the method
     * @param args the args
     */
    public RPCMessageRequest(Method method,Object[] args) {
        this.method = method;
        this.args = args;
        this.entry = null;
        this.isRollBackAction = false;
    }

    /**
     * Instantiates a new RPC message request.
     *
     * @param method the method
     * @param args the args
     * @param entry the entry
     */
    public RPCMessageRequest(Method method,Object[] args, SuperEntry entry) {
        this.method = method;
        this.entry = entry;
        this.args = args;
        this.isRollBackAction = false;
    }

    /**
     * Instantiates a new RPC message request.
     *
     * @param method the method
     * @param args the args
     * @param entry the entry
     * @param isRollBackAction the is roll back action
     */
    public RPCMessageRequest(Method method,Object[] args, SuperEntry entry, Boolean isRollBackAction) {
        this.method = method;
        this.entry = entry;
        this.args = args;
        this.isRollBackAction = isRollBackAction;
    }

    /**
     * Gets the method.
     *
     * @return the method
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Sets the method.
     *
     * @param op the new method
     */
    public void setMethod(Method op) {
        this.method = op;
    }

    /**
     * Gets the args.
     *
     * @return the args
     */
    public Object[] getArgs() {
        return args;
    }

    /**
     * Sets the args.
     *
     * @param args the new args
     */
    public void setArgs(Object[] args) {
        this.args = args;
    }

    /**
     * Gets the entry.
     *
     * @return the entry
     */
    public SuperEntry getEntry() {
        return entry;
    }

    /**
     * Sets the entry.
     *
     * @param entry the new entry
     */
    public void setEntry(SuperEntry entry) {
        this.entry = entry;
    }

    /**
     * Gets the checks if is roll back action.
     *
     * @return the checks if is roll back action
     */
    public Boolean getIsRollBackAction() {
        return isRollBackAction;
    }
}

