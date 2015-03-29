package ac.at.tuwien.sbc.domain.util;

import org.mozartspaces.capi3.AnyCoordinator;
import org.mozartspaces.capi3.Coordinator;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.ImplicitCoordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsCoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by dietl_ma on 25/03/15.
 */
public abstract class SpaceUtils {

    private static final Logger logger = LoggerFactory.getLogger(SpaceUtils.class);

    /**
     * Get or create named container
     * Inspired by mozartspace tutorial
     * @param space
     * @param containerName
     * @param capi
     * @param coordinator
     * @return
     * @throws MzsCoreException
     */
    public static ContainerReference getOrCreateNamedContainer(final URI space, final String containerName,
                                                               final Capi capi, final Coordinator coordinator) throws MzsCoreException {

        ContainerReference cref;
        try {
            // Get the Container
            logger.info("Lookup container: " + containerName);
            cref = capi.lookupContainer(containerName, space, MzsConstants.RequestTimeout.DEFAULT, null);
            logger.info("Container found: " + containerName);
            // If it is unknown, create it
        } catch (MzsCoreException e) {
            logger.info("Container not found, creating it ...");
            // Create the Container
            ArrayList<Coordinator> obligatoryCoords = new ArrayList<Coordinator>();
            obligatoryCoords.add(coordinator);
            cref = capi.createContainer(containerName, space, MzsConstants.Container.UNBOUNDED, obligatoryCoords, null, null);
            logger.info("Container created: " + containerName);
        }
        return cref;
    }

    /**
     * Overloaded for testing purposes
     * Get or create named container
     * Inspired by mozartspace tutorial
     * @param space
     * @param containerName
     * @param capi
     * @param coordinator
     * @return
     * @throws MzsCoreException
     */
    public static ContainerReference getOrCreateNamedContainer(final URI space, final String containerName,
                                                               final Capi capi, final Coordinator coordinator, final Coordinator... optionalCoordinators) throws MzsCoreException {

        ContainerReference cref;
        try {
            // Get the Container
            logger.info("Lookup container: " + containerName);
            cref = capi.lookupContainer(containerName, space, MzsConstants.RequestTimeout.DEFAULT, null);
            logger.info("Container found: " + containerName);
            // If it is unknown, create it
        } catch (MzsCoreException e) {
            logger.info("Container not found, creating it ...");
            // Create the Container
            ArrayList<Coordinator> obligatoryCoords = new ArrayList<Coordinator>();
            obligatoryCoords.add(coordinator);

            ArrayList<Coordinator> optionalCoords = new ArrayList<>(Arrays.asList(optionalCoordinators));
            cref = capi.createContainer(containerName, space, MzsConstants.Container.UNBOUNDED, obligatoryCoords, optionalCoords, null);
            logger.info("Container created: " + containerName);
        }
        return cref;
    }
}
