package ac.at.tuwien.sbc.domain.messaging;

/**
 * Created by dietl_ma on 01/04/15.
 */
public class RPCMessageResponse<T> {

    private T response;

    public RPCMessageResponse(T response) {
        this.response = response;
    }

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }
}
