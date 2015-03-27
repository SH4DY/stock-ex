package workflow.space;

import ac.at.tuwien.sbc.domain.entry.ReleaseEntry;
import org.mozartspaces.capi3.RandomCoordinator;
import org.mozartspaces.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import workflow.IReleaseService;

/**
 * Created by shady on 27/03/15.
 */

@Service
@Profile("space")
public class SpaceReleaseService implements IReleaseService {
    @Autowired
    MzsCore core;

    @Autowired
    Capi capi;

    @Autowired
    @Qualifier("releaseContainer")
    ContainerReference cref;

    private static final Logger logger = LoggerFactory.getLogger(SpaceReleaseService.class);

    @Override
    public void makeRelease(ReleaseEntry rls) {
        Entry entry = new Entry(rls);
        try {
            capi.write(cref, MzsConstants.RequestTimeout.TRY_ONCE,null,entry);
        } catch (MzsCoreException e) {
            logger.error("Exception occurred while trying to write ReleaseEntry to container");
        }
    }
}
