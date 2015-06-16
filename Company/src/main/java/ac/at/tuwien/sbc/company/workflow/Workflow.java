package ac.at.tuwien.sbc.company.workflow;

import ac.at.tuwien.sbc.company.configuration.MarketArgsMapper;
import ac.at.tuwien.sbc.domain.configuration.MarketArgsConfiguration;
import ac.at.tuwien.sbc.domain.entry.ReleaseEntry;
import ac.at.tuwien.sbc.domain.enums.ShareType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

// TODO: Auto-generated Javadoc
/**
 * Created by shady on 27/03/15.
 */
@Service
public class Workflow {

    /** The company id. */
    @Value("${id}")
    private String companyID;

    @Autowired
    private MarketArgsConfiguration<MarketArgsMapper.MarketArgs> marketArgs;

    /** The rls service. */
    @Autowired
    IReleaseService rlsService;



    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(Workflow.class);

    /** The template. */
    @Autowired
    RabbitTemplate template;

    /**
     * On post construct.
     */
    @PostConstruct
    public void onPostConstruct() {

        Integer numShares = marketArgs.getArgsByMarket(marketArgs.getMarkets().get(0)).getNumShares();
        Double initPrice  = marketArgs.getArgsByMarket(marketArgs.getMarkets().get(0)).getInitPrice();
        logger.info("Company " + companyID + " entered , wants to release "
                + numShares + " with initial price of " +
                initPrice + " per share");

        ReleaseEntry releaseEntry = new ReleaseEntry();
        releaseEntry.setCompanyID(companyID);
        releaseEntry.setNumShares(numShares);
        releaseEntry.setPrice(initPrice);
        releaseEntry.setShareType(ShareType.SHARE);

        rlsService.makeRelease(releaseEntry);
        logger.error("Written");
    }
}
