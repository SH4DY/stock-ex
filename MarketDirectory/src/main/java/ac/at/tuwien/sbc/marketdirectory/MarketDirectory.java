package ac.at.tuwien.sbc.marketdirectory;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dietl_ma on 16/06/15.
 */
@Service
public class MarketDirectory {

    List<String> directory = new ArrayList<>();

    public List<String> getDirectory() {
        return directory;
    }

    public void setDirectory(List<String> directory) {
        this.directory = directory;
    }
}
