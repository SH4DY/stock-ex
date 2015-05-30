import ac.at.tuwien.sbc.company.workflow.IReleaseService;
import ac.at.tuwien.sbc.company.workflow.space.SpaceReleaseService;
import ac.at.tuwien.sbc.domain.configuration.CommonSpaceConfiguration;
import ac.at.tuwien.sbc.domain.entry.ReleaseEntry;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.MzsCore;
import org.mozartspaces.core.MzsCoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;

import static org.mozartspaces.core.MzsConstants.RequestTimeout;
import static org.mozartspaces.core.MzsConstants.Selecting;

/**
 * Created by shady on 27/03/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {CommonSpaceConfiguration.class, SpaceReleaseService.class})
@ActiveProfiles(profiles="space")
public class CompanyTest {

    @Autowired
    Capi capi;

    @Autowired
    MzsCore core;

    @Autowired
    @Qualifier("releaseContainer")
    ContainerReference cref;

    @Autowired
    IReleaseService rlsService;

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(CompanyTest.class);

    @Before
    public void setUp(){
        int deletedEntries = 0;
        //Delete ALL releases from container to start
        try {
            deletedEntries = capi.delete(cref, FifoCoordinator.newSelector(Selecting.COUNT_ALL), RequestTimeout.INFINITE, null);
        } catch (MzsCoreException e) {
            logger.error("Exception while trying to delete alle entries from releaseContainer");
        }
        //Generate 3 releases and store them in container

        ReleaseEntry rls1 = new ReleaseEntry();
        rls1.setCompanyID("GOOG");
        rls1.setNumShares(200);
        rls1.setPrice(400.00);

        ReleaseEntry rls2 = new ReleaseEntry();
        rls2.setCompanyID("MSFT");
        rls2.setNumShares(1);
        rls2.setPrice(4.00);

        ReleaseEntry rls3 = new ReleaseEntry();
        rls3.setCompanyID("YAHO");
        rls3.setNumShares(0);
        rls3.setPrice(99999999999.99);

        rlsService.makeRelease(rls1);
        rlsService.makeRelease(rls2);
        rlsService.makeRelease(rls3);
    }

    @Test
    public void testExactReleaseExistence(){
        ArrayList<ReleaseEntry> resultEntries = null;
        try {
            resultEntries = capi.read(cref, FifoCoordinator.newSelector(Selecting.COUNT_ALL), RequestTimeout.INFINITE, null);
            for(ReleaseEntry rls : resultEntries){
                logger.info("Test: Release Entry " + rls.getCompanyID() + " with numShares " + rls.getNumShares() + " and initPrice of " + rls.getPrice());
            }

        } catch (MzsCoreException e) {
            logger.error("Exception while FIFO accessing space to test existence of releases");
        }
        Assert.assertTrue(resultEntries.size() == 3);
    }

}
