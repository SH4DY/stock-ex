package ac.at.tuwien.sbc.domain.configuration;

import ac.at.tuwien.sbc.domain.util.SpaceUtils;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 25/03/15.
 */
@Configuration
@Profile("space")
public class CommonSpaceConfiguration {

    /** The Constant SPACE_URI. */
    @Deprecated
    public static final String SPACE_URI =  "xvsm://localhost:9876";

    /** The Constant SPACE_URI_PREFIX. */
    public static final String SPACE_URI_PREFIX =  "xvsm://";

    public static final String DEPOT_CONTAINER =  "depotContainer";
    public static final String ORDER_CONTAINER =  "orderContainer";
    public static final String RELEASE_CONTAINER =  "releaseContainer";
    public static final String SHARE_CONTAINER =  "shareContainer";
    public static final String TRANSACTION_CONTAINER =  "transactionContainer";

    public static final String DEPOT_CONTAINER_MAP =  "depotContainerMap";
    public static final String ORDER_CONTAINER_MAP =  "orderContainerMap";
    public static final String RELEASE_CONTAINER_MAP =  "releaseContainerMap";
    public static final String SHARE_CONTAINER_MAP =  "shareContainerMap";
    public static final String TRANSACTION_CONTAINER_MAP =  "transactionContainerMap";

    @Autowired
    private MarketArgsConfiguration marketArgs;

    /**
     * Core.
     *
     * @return the mzs core
     */
    @Bean MzsCore core() {
        return DefaultMzsCore.newInstanceWithoutSpace();
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
    @Bean(name = DEPOT_CONTAINER_MAP)
    public HashMap<String, ContainerReference> depotContainer(Capi capi) throws URISyntaxException, MzsCoreException {

        HashMap<String, ContainerReference> map = new HashMap<>();
        for (String market : (ArrayList<String>)marketArgs.getMarkets()) {
            URI uri = new URI(SPACE_URI_PREFIX + market);
            ArrayList<Coordinator> coordinators = new ArrayList<Coordinator>();
            coordinators.add(new KeyCoordinator());
            ContainerReference ref = SpaceUtils.getOrCreateNamedContainer(uri, DEPOT_CONTAINER, capi, coordinators);
            if (ref != null)
                map.put(market, ref);
        }
        return map;
    }

    @Bean(name = DEPOT_CONTAINER)
    public ContainerReference getSingleDepotContainer(@Qualifier("depotContainerMap") HashMap<String, ContainerReference> container) {
        return container.entrySet().iterator().next().getValue();
    }

    /**
     * Order container.
     *
     * @param capi the capi
     * @return the container reference
     * @throws URISyntaxException the URI syntax exception
     * @throws MzsCoreException the mzs core exception
     */
    @Bean(name = ORDER_CONTAINER_MAP)
    public HashMap<String, ContainerReference> orderContainer(Capi capi) throws URISyntaxException, MzsCoreException {

        HashMap<String, ContainerReference> map = new HashMap<>();
        for (String market : (ArrayList<String>)marketArgs.getMarkets()) {
            URI uri = new URI(SPACE_URI_PREFIX + market);
            ArrayList<Coordinator> coordinators = new ArrayList<Coordinator>();
            coordinators.add(new LindaCoordinator(false));
            coordinators.add(new FifoCoordinator());
            coordinators.add(new RandomCoordinator());
            coordinators.add(new QueryCoordinator());
            ContainerReference ref = SpaceUtils.getOrCreateNamedContainer(uri, ORDER_CONTAINER, capi, coordinators);
            if (ref != null)
                map.put(market, ref);
        }
        return map;
    }

    @Bean(name = ORDER_CONTAINER)
    public ContainerReference getSingleOrderContainer(@Qualifier(ORDER_CONTAINER_MAP) HashMap<String, ContainerReference> container) {
        return container.entrySet().iterator().next().getValue();
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
    @Bean(name = RELEASE_CONTAINER_MAP)
    public HashMap<String, ContainerReference> releaseContainer(Capi capi) throws URISyntaxException, MzsCoreException {
        HashMap<String, ContainerReference> map = new HashMap<>();
        for (String market : (ArrayList<String>)marketArgs.getMarkets()) {
            URI uri = new URI(SPACE_URI_PREFIX + market);
            ArrayList<Coordinator> coordinators= new ArrayList<Coordinator>();
            coordinators.add(new FifoCoordinator());
            ContainerReference ref = SpaceUtils.getOrCreateNamedContainer(uri, RELEASE_CONTAINER, capi, coordinators);
            if (ref != null)
                map.put(market, ref);
        }
        return map;
    }

    @Bean(name = RELEASE_CONTAINER)
    public ContainerReference getSingleReleaseContainer(@Qualifier(RELEASE_CONTAINER_MAP) HashMap<String, ContainerReference> container) {
        return container.entrySet().iterator().next().getValue();
    }
    /*public ContainerReference getSingleReleaseContainer(Capi capi) throws URISyntaxException, MzsCoreException {
        HashMap<String, ContainerReference> map = releaseContainer(capi);
        return map.entrySet().iterator().next().getValue();
    }*/

    /**
     * Share container.
     *
     * @param capi the capi
     * @return the container reference
     * @throws URISyntaxException the URI syntax exception
     * @throws MzsCoreException the mzs core exception
     */
    @Bean(name = SHARE_CONTAINER_MAP)
    public HashMap<String, ContainerReference> shareContainer(Capi capi) throws URISyntaxException, MzsCoreException {
        HashMap<String, ContainerReference> map = new HashMap<>();
        for (String market : (ArrayList<String>)marketArgs.getMarkets()) {
            URI uri = new URI(SPACE_URI_PREFIX + market);
            ArrayList<Coordinator> coordinators= new ArrayList<Coordinator>();
            coordinators.add(new KeyCoordinator());
            coordinators.add(new FifoCoordinator());
            ContainerReference ref = SpaceUtils.getOrCreateNamedContainer(uri, SHARE_CONTAINER, capi, coordinators);
            if (ref != null)
                map.put(market, ref);
        }
        return map;
    }

    @Bean(name = SHARE_CONTAINER)
    public ContainerReference getSingleShareContainer(@Qualifier(SHARE_CONTAINER_MAP) HashMap<String, ContainerReference> container) {
        return container.entrySet().iterator().next().getValue();
    }

    /**
     * Transaction container.
     *
     * @param capi the capi
     * @return the container reference
     * @throws URISyntaxException the URI syntax exception
     * @throws MzsCoreException the mzs core exception
     */
    @Bean(name = TRANSACTION_CONTAINER_MAP)
    public HashMap<String, ContainerReference> transactionContainer(Capi capi) throws URISyntaxException, MzsCoreException {
        HashMap<String, ContainerReference> map = new HashMap<>();
        for (String market : (ArrayList<String>)marketArgs.getMarkets()) {
            URI uri = new URI(SPACE_URI_PREFIX + market);
            ArrayList<Coordinator> coordinators= new ArrayList<Coordinator>();
            coordinators.add(new FifoCoordinator());
            ContainerReference ref = SpaceUtils.getOrCreateNamedContainer(uri, TRANSACTION_CONTAINER, capi, coordinators);
            if (ref != null)
                map.put(market, ref);
        }
        return map;
    }

    @Bean(name = TRANSACTION_CONTAINER)
    public ContainerReference getSingleTransactionContainer(@Qualifier(TRANSACTION_CONTAINER_MAP) HashMap<String, ContainerReference> container) {
        return container.entrySet().iterator().next().getValue();
    }
}
