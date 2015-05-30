package ac.at.tuwien.sbc.company.workflow.space;

import ac.at.tuwien.sbc.domain.entry.ReleaseEntry;
import org.mozartspaces.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

// TODO: Auto-generated Javadoc
/**
 * Created by shady on 27/03/15.
 */

@Service
@Profile("space")
public class SpaceReleaseService implements ac.at.tuwien.sbc.company.workflow.IReleaseService {
    
    /** The core. */
    @Autowired
    MzsCore core;

    /** The capi. */
    @Autowired
    Capi capi;

    /** The releaseContainer. */
    @Autowired
    @Qualifier("releaseContainer")
    ContainerReference releaseContainer;

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(SpaceReleaseService.class);

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.company.workflow.IReleaseService#makeRelease(ac.at.tuwien.sbc.domain.entry.ReleaseEntry)
     */
    @Override
    public void makeRelease(ReleaseEntry rls) {
        Entry entry = new Entry(rls);
        try {
            capi.write(releaseContainer, MzsConstants.RequestTimeout.TRY_ONCE,null,entry);
        } catch (MzsCoreException e) {
            logger.error("Exception occurred while trying to write ReleaseEntry to container");
        }
    }
}
