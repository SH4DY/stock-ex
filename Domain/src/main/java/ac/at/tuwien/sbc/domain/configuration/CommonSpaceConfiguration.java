package ac.at.tuwien.sbc.domain.configuration;

import ac.at.tuwien.sbc.domain.util.SpaceUtils;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 25/03/15.
 */
@Configuration
@Profile("space")
public class CommonSpaceConfiguration {

    /** The Constant SPACE_URI. */
    public static final String SPACE_URI =  "xvsm://localhost:9876";

    /**
     * Core.
     *
     * @return the mzs core
     */
    @Bean MzsCore core() {
        return DefaultMzsCore.newInstance(0);
    }

    /**
     * Capi.
     *
     * @param core the core
     * @return the capi
     */
    @Bean
    public Capi capi(MzsCore core) {
        return new Capi(core);
    }

    /**
     * Investor depot container.
     *
     * @param capi the capi
     * @return the container reference
     * @throws URISyntaxException the URI syntax exception
     * @throws MzsCoreException the mzs core exception
     */
    @Bean(name = "investorDepotContainer")
    public ContainerReference investorDepotContainer(Capi capi) throws URISyntaxException, MzsCoreException {
        URI uri = new URI(SPACE_URI);
        ArrayList<Coordinator> coordinators= new ArrayList<Coordinator>();
        coordinators.add(new KeyCoordinator());
        return SpaceUtils.getOrCreateNamedContainer(uri, "investorDepotContainer", capi, coordinators);
    }

    /**
     * Order container.
     *
     * @param capi the capi
     * @return the container reference
     * @throws URISyntaxException the URI syntax exception
     * @throws MzsCoreException the mzs core exception
     */
    @Bean(name = "orderContainer")
    public ContainerReference orderContainer(Capi capi) throws URISyntaxException, MzsCoreException {
        URI uri = new URI(SPACE_URI);
        ArrayList<Coordinator> coordinators= new ArrayList<Coordinator>();
        coordinators.add(new LindaCoordinator(false));
        coordinators.add(new FifoCoordinator());
        coordinators.add(new RandomCoordinator());
        coordinators.add(new QueryCoordinator());
        return SpaceUtils.getOrCreateNamedContainer(uri, "orderContainer", capi, coordinators);
    }

    //Companies release their shares into this space
    //Broker can observe it and generate Orders out of them
    /**
     * Release container.
     *
     * @param capi the capi
     * @return the container reference
     * @throws URISyntaxException the URI syntax exception
     * @throws MzsCoreException the mzs core exception
     */
    @Bean(name = "releaseContainer")
    public ContainerReference releaseContainer(Capi capi) throws URISyntaxException, MzsCoreException {
        URI uri = new URI(SPACE_URI);
        ArrayList<Coordinator> coordinators= new ArrayList<Coordinator>();
        coordinators.add(new FifoCoordinator());
        return SpaceUtils.getOrCreateNamedContainer(uri, "releaseContainer", capi, coordinators);
    }

    /**
     * Share container.
     *
     * @param capi the capi
     * @return the container reference
     * @throws URISyntaxException the URI syntax exception
     * @throws MzsCoreException the mzs core exception
     */
    @Bean(name = "shareContainer")
    public ContainerReference shareContainer(Capi capi) throws URISyntaxException, MzsCoreException {
        URI uri = new URI(SPACE_URI);
        ArrayList<Coordinator> coordinators= new ArrayList<Coordinator>();
        coordinators.add(new KeyCoordinator());
        coordinators.add(new FifoCoordinator());
        return SpaceUtils.getOrCreateNamedContainer(uri, "shareContainer", capi, coordinators);
    }

    /**
     * Transaction container.
     *
     * @param capi the capi
     * @return the container reference
     * @throws URISyntaxException the URI syntax exception
     * @throws MzsCoreException the mzs core exception
     */
    @Bean(name = "transactionContainer")
    public ContainerReference transactionContainer(Capi capi) throws URISyntaxException, MzsCoreException {
        URI uri = new URI(SPACE_URI);
        ArrayList<Coordinator> coordinators= new ArrayList<Coordinator>();
        coordinators.add(new FifoCoordinator());
        return SpaceUtils.getOrCreateNamedContainer(uri, "transactionContainer", capi, coordinators);
    }

}
