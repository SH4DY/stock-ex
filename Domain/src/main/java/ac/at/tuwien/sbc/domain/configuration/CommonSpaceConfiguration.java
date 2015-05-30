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
    public static final String SPACE_URI =  "xvsm://localhost:9876";

    /** The Constant SPACE_URI_PREFIX. */
    public static final String SPACE_URI_PREFIX =  "xvsm://";

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


    @PostConstruct
    public void onPostConstruct() {
        for (String market : (ArrayList<String>)marketArgs.getMarkets()) {
            System.out.println("MARKET: " + market);
        }
    }
    /**
     * Investor depot container.
     *
     * @param capi the capi
     * @return the container reference
     * @throws URISyntaxException the URI syntax exception
     * @throws MzsCoreException the mzs core exception
     */
    @Bean(name = "depotContainerMap")
    public HashMap<String, ContainerReference> depotContainer(Capi capi) throws URISyntaxException, MzsCoreException {

        HashMap<String, ContainerReference> map = new HashMap<>();
        for (String market : (ArrayList<String>)marketArgs.getMarkets()) {
            URI uri = new URI(SPACE_URI_PREFIX + market);
            ArrayList<Coordinator> coordinators = new ArrayList<Coordinator>();
            coordinators.add(new KeyCoordinator());
            ContainerReference ref = SpaceUtils.getOrCreateNamedContainer(uri, "depotContainer", capi, coordinators);
            if (ref != null)
                map.put(market, ref);
        }
        return map;
    }

    @Bean(name = "depotContainer")
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
    @Bean(name = "orderContainerMap")
    public HashMap<String, ContainerReference> orderContainer(Capi capi) throws URISyntaxException, MzsCoreException {

        HashMap<String, ContainerReference> map = new HashMap<>();
        for (String market : (ArrayList<String>)marketArgs.getMarkets()) {
            URI uri = new URI(SPACE_URI_PREFIX + market);
            ArrayList<Coordinator> coordinators = new ArrayList<Coordinator>();
            coordinators.add(new LindaCoordinator(false));
            coordinators.add(new FifoCoordinator());
            coordinators.add(new RandomCoordinator());
            coordinators.add(new QueryCoordinator());
            ContainerReference ref = SpaceUtils.getOrCreateNamedContainer(uri, "orderContainer", capi, coordinators);
            if (ref != null)
                map.put(market, ref);
        }
        return map;
    }

    @Bean(name = "orderContainer")
    public ContainerReference getSingleOrderContainer(@Qualifier("orderContainerMap") HashMap<String, ContainerReference> container) {
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
    @Bean(name = "releaseContainerMap")
    public HashMap<String, ContainerReference> releaseContainer(Capi capi) throws URISyntaxException, MzsCoreException {
        HashMap<String, ContainerReference> map = new HashMap<>();
        for (String market : (ArrayList<String>)marketArgs.getMarkets()) {
            URI uri = new URI(SPACE_URI_PREFIX + market);
            ArrayList<Coordinator> coordinators= new ArrayList<Coordinator>();
            coordinators.add(new FifoCoordinator());
            ContainerReference ref = SpaceUtils.getOrCreateNamedContainer(uri, "releaseContainer", capi, coordinators);
            if (ref != null)
                map.put(market, ref);
        }
        return map;
    }

    @Bean(name = "releaseContainer")
    public ContainerReference getSingleReleaseContainer( @Qualifier("releaseContainerMap") HashMap<String, ContainerReference> container) {
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
    @Bean(name = "shareContainerMap")
    public HashMap<String, ContainerReference> shareContainer(Capi capi) throws URISyntaxException, MzsCoreException {
        HashMap<String, ContainerReference> map = new HashMap<>();
        for (String market : (ArrayList<String>)marketArgs.getMarkets()) {
            URI uri = new URI(SPACE_URI_PREFIX + market);
            ArrayList<Coordinator> coordinators= new ArrayList<Coordinator>();
            coordinators.add(new KeyCoordinator());
            coordinators.add(new FifoCoordinator());
            ContainerReference ref = SpaceUtils.getOrCreateNamedContainer(uri, "shareContainer", capi, coordinators);
            if (ref != null)
                map.put(market, ref);
        }
        return map;
    }

    @Bean(name = "shareContainer")
    public ContainerReference getSingleShareContainer(@Qualifier("shareContainerMap") HashMap<String, ContainerReference> container) {
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
    @Bean(name = "transactionContainerMap")
    public HashMap<String, ContainerReference> transactionContainer(Capi capi) throws URISyntaxException, MzsCoreException {
        HashMap<String, ContainerReference> map = new HashMap<>();
        for (String market : (ArrayList<String>)marketArgs.getMarkets()) {
            URI uri = new URI(SPACE_URI_PREFIX + market);
            ArrayList<Coordinator> coordinators= new ArrayList<Coordinator>();
            coordinators.add(new FifoCoordinator());
            ContainerReference ref = SpaceUtils.getOrCreateNamedContainer(uri, "transactionContainer", capi, coordinators);
            if (ref != null)
                map.put(market, ref);
        }
        return map;
    }

    @Bean(name = "transactionContainer")
    public ContainerReference getSingleTransactionContainer(@Qualifier("transactionContainerMap") HashMap<String, ContainerReference> container) {
        return container.entrySet().iterator().next().getValue();
    }


}
