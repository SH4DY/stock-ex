package ac.at.tuwien.sbc.market.gui.models;

import ac.at.tuwien.sbc.domain.entry.ShareEntry;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * TableModel to visualize the OverviewTable on the Main GUI
 * which displays all shares, their price and their volume on the market.
 * Created by shady on 05/04/15.
 */
public class ShareTableModel extends AbstractTableModel{
    String[] columnNames = {"Share ID", "Volume", "Stockprice"};

    LinkedHashSet<ShareEntry> content = new LinkedHashSet<>();

    public ShareTableModel(List<ShareEntry> shareEntries){
        if(shareEntries != null) {
            content.addAll(content);
        }
    }

    public void addRow(ShareEntry shareEntry){
        if(content.contains(shareEntry)){
            content.remove(shareEntry);
        }
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
        Iterator<ShareEntry> it = content.iterator();

        for(int i = 0; i < rowIndex; i++) it.next();

        ShareEntry entry = it.next();

        switch (columnIndex) {
            case 0: return entry.getShareID();
            case 1: return entry.getNumShares();
            case 2: return entry.getPrice();
        }
        return null;
    }
}
