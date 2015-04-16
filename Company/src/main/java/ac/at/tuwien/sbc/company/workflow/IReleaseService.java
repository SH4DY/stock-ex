package ac.at.tuwien.sbc.company.workflow;

import ac.at.tuwien.sbc.domain.entry.ReleaseEntry;

// TODO: Auto-generated Javadoc
/**
 * Created by shady on 27/03/15.
 */
public interface IReleaseService {

    /**
     * Make release.
     *
     * @param rls the ReleaseEntry
     */
    public void makeRelease(ReleaseEntry rls);
}
