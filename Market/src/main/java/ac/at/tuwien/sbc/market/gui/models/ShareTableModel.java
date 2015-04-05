package ac.at.tuwien.sbc.market.gui.models;

import ac.at.tuwien.sbc.domain.entry.ShareEntry;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TableModel to visualize the OverviewTable on the Main GUI
 * which displays all shares, their price and their volume on the market.
 * Created by shady on 05/04/15.
 */
public class ShareTableModel extends AbstractTableModel{

    String[] columnNames = {"Share ID", "Volume", "Stockprice"};

    Set<ShareEntry> content = new HashSet<>();

    public ShareTableModel(List<ShareEntry> shareEntries){
        if(shareEntries != null) {
            for(ShareEntry entry : shareEntries)
            content.;
        }else{
            content = new ArrayList<>();
        }
    }

    public void addRow(ShareEntry shareEntry){
        content.add(shareEntry);
        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getRowCount() {
        return content.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0: return content.get(rowIndex).getShareID();
            case 1: return content.get(rowIndex).getNumShares();
            case 2: return content.get(rowIndex).getPrice();
        }
        return null;
    }
}
