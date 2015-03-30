package ac.at.tuwien.sbc.domain.exception;

import ac.at.tuwien.sbc.domain.event.CoordinationListener;

/**
 * Created by dietl_ma on 29/03/15.
 */
public class CoordinationServiceException extends Exception {
    public CoordinationServiceException() {}

    public CoordinationServiceException(String message) {
        super(message);
    }
}
