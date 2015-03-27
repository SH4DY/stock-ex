package workflow;

import ac.at.tuwien.sbc.domain.entry.ReleaseEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by shady on 27/03/15.
 */
@Component
public class Workflow {

    @Autowired
    IReleaseService rlsService;

    @Value("${id}")
    private String companyID;

    @Value("${numShares}")
    private Integer numShares;

    @Value("${initPrice}")
    private Double initPrice;


    private static final Logger logger = LoggerFactory.getLogger(Workflow.class);

    @PostConstruct
    private void onPostConstruct() {

        logger.info("Company" + companyID + " entered , wants to release "
                + numShares + " with initial price of " +
                initPrice + " per share");

        ReleaseEntry releaseEntry = new ReleaseEntry();
        releaseEntry.setCompanyID(companyID);
        releaseEntry.setNumShares(numShares);
        releaseEntry.setPrice(initPrice);

        rlsService.makeRelease(releaseEntry);
    }

}
