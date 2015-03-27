package ac.at.tuwien.sbc.domain.configuration;

import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import ac.at.tuwien.sbc.domain.util.SpaceUtils;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.KeyCoordinator;
import org.mozartspaces.capi3.LindaCoordinator;
import org.mozartspaces.capi3.RandomCoordinator;
import org.mozartspaces.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by dietl_ma on 25/03/15.
 */
@Configuration
@Profile("space")
public class CommonSpaceConfiguration {

    public static final String SPACE_URI =  "xvsm://localhost:9876";

    @Bean MzsCore core() {
        return DefaultMzsCore.newInstance(0);
    }

    @Bean
    public Capi capi(MzsCore core) {
        return new Capi(core);
    }

    @Bean(name = "defaultContainer")
    public ContainerReference defaultContainer(Capi capi) throws URISyntaxException, MzsCoreException {
        URI uri = new URI(SPACE_URI);
        return SpaceUtils.getOrCreateNamedContainer(uri, "defaultContainer", capi, new FifoCoordinator());
    }

    @Bean(name = "investorDepotContainer")
    public ContainerReference investorDepotContainer(Capi capi) throws URISyntaxException, MzsCoreException {
        URI uri = new URI(SPACE_URI);
        return SpaceUtils.getOrCreateNamedContainer(uri, "defaultContainer", capi, new KeyCoordinator());
    }

    @Bean(name = "orderContainer")
    public ContainerReference orderContainer(Capi capi) throws URISyntaxException, MzsCoreException {
        URI uri = new URI(SPACE_URI);
        return SpaceUtils.getOrCreateNamedContainer(uri, "orderContainer", capi, new LindaCoordinator(false));
    }

    //Companies release their shares into this space
    //Broker can observe it and generate Orders out of them
    @Bean(name = "releaseContainer")
    public ContainerReference releaseContainer(Capi capi) throws URISyntaxException, MzsCoreException {
        URI uri = new URI(SPACE_URI);
        return SpaceUtils.getOrCreateNamedContainer(uri, "releaseContainer", capi, new FifoCoordinator());
    }

}
