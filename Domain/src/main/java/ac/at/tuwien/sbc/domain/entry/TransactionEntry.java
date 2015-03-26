package ac.at.tuwien.sbc.domain.entry;

import org.springframework.scheduling.annotation.Scheduled;

import java.io.Serializable;

/**
 * Created by dietl_ma on 25/03/15.
 */
//FIFO CONTAINER
public class TransactionEntry implements Serializable {

    private Integer transactionID;

    private Integer brokerID;

    private Integer buyerID;

    private Integer sellerID;

    private Integer shareID;

    private Integer sellOrderID;

    private Integer buyOrderID;

    private Double price;

    private Integer numShares;

    private Double sumPrice;

    private Double provision;

}
