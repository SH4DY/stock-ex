import ac.at.tuwien.sbc.domain.configuration.CommonRabbitConfiguration;
import ac.at.tuwien.sbc.domain.configuration.CommonSpaceConfiguration;
import ac.at.tuwien.sbc.market.MarketApp;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mozartspaces.capi3.AnyCoordinator;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;

/**
 * Created by shady on 26/03/15.
 */


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CommonSpaceConfiguration.class)
@ActiveProfiles("space")
public class MarketInitTests {

    @Autowired
    Capi capi;

    @Autowired
    MzsCore core;

    @Autowired
    @Qualifier("defaultContainer")
    ContainerReference defaultContainer;

    @Before
    public void fillSpace(){
        try {
            capi.write(defaultContainer, new Entry("Market enters the space!"));

        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void readEntries() throws Exception {
//        capi.write(defaultContainer, new Entry("mooooooore!"));
//        ArrayList<Entry> resultEntries = capi.read(defaultContainer, FifoCoordinator.newSelector(0),10,null);
//        Assert.assertTrue(!resultEntries.isEmpty());
//        System.out.println("Entry read: " + resultEntries.get(0));
    }


}
