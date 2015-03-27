import ac.at.tuwien.sbc.domain.configuration.CommonSpaceConfiguration;
import ac.at.tuwien.sbc.domain.entry.ReleaseEntry;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.Selector;
import org.mozartspaces.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import workflow.space.SpaceReleaseService;

import java.util.ArrayList;

/**
 * Created by shady on 27/03/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CommonSpaceConfiguration.class)
@ActiveProfiles("space")
public class CompanyTest {

    @Autowired
    Capi capi;

    @Autowired
    MzsCore core;

    @Autowired
    @Qualifier("releaseContainer")
    ContainerReference cref;

    @Autowired
    SpaceReleaseService rlsService;

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(CompanyTest.class);

    @Before
    public void setUp(){
        //Generate some released and put them into the space

        ReleaseEntry rls1 = new ReleaseEntry();
        rls1.setCompanyID("GOOG");
        rls1.setNumShares(200);
        rls1.setPrice(400.00);

        ReleaseEntry rls2 = new ReleaseEntry();
        rls2.setCompanyID("MSFT");
        rls2.setNumShares(1);
        rls2.setPrice(4.00);

        ReleaseEntry rls3 = new ReleaseEntry();
        rls3.setCompanyID("GOOG");
        rls3.setNumShares(0);
        rls3.setPrice(99999999999.99);

        rlsService.makeRelease(rls1);
        rlsService.makeRelease(rls2);
        rlsService.makeRelease(rls3);
    }

    @Test
    public void testReleaseExistence(){
        ArrayList<Selector> selectors = new ArrayList<Selector>();
        selectors.add(FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL));
        try {
            ArrayList<String> resultEntries = capi.read(cref, selectors, MzsConstants.RequestTimeout.TRY_ONCE, null);
            Assert.assertTrue(resultEntries.size() == 3);

        } catch (MzsCoreException e) {
            System.out.println("Exception while FIFO accessing space to test existence of releases");
        }
    }

}
