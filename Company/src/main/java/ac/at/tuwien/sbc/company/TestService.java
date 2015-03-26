package ac.at.tuwien.sbc.company;

import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.MzsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

/**
 * Created by dietl_ma on 25/03/15.
 */
@Service
public class TestService {

    @Autowired
    @Qualifier("defaultContainer")
    ContainerReference cRef;

    @Value("${company_id}")
    private String companyId;

    @Autowired
    Capi capi;
    @PostConstruct

    private void postConstruct() {

        System.out.println("TESTSERVICE:" + companyId);

        ArrayList<ShareEntry> entries = null;
        try {
            entries = capi.take(cRef, FifoCoordinator.newSelector(), MzsConstants.RequestTimeout.INFINITE, null);
        } catch (Exception ex) {
            System.out.println("transaction timeout. retry.");
        }

        if (entries != null) {
           ShareEntry entry = entries.get(0);
           System.out.println("TAKEN SHARE ENTRY:" + entry.getShareID());
        }


    }
}
