package ac.at.tuwien.sbc.company.workflow;

import ac.at.tuwien.sbc.domain.entry.ReleaseEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by shady on 27/03/15.
 */
@Service
public class Workflow {

    @Value("${id}")
    private String companyID;

    @Value("${numShares}")
    private Integer numShares;

    @Value("${initPrice}")
    private Double initPrice;

    @Autowired
    IReleaseService rlsService;

    private static final Logger logger = LoggerFactory.getLogger(Workflow.class);

    @Autowired
    RabbitTemplate template;

    @PostConstruct
    public void onPostConstruct() {

        logger.info("Company " + companyID + " entered , wants to release "
                + numShares + " with initial price of " +
                initPrice + " per share");

        ReleaseEntry releaseEntry = new ReleaseEntry();
        releaseEntry.setCompanyID(companyID);
        releaseEntry.setNumShares(numShares);
        releaseEntry.setPrice(initPrice);

        rlsService.makeRelease(releaseEntry);
    }
}
