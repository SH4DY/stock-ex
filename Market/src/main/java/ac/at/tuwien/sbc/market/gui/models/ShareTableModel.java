package ac.at.tuwien.sbc.market.gui.models;

import ac.at.tuwien.sbc.domain.entry.ShareEntry;

import javax.swing.table.AbstractTableModel;
import java.util.*;

// TODO: Auto-generated Javadoc
/**
 * TableModel to visualize the OverviewTable on the Main GUI
 * which displays all shares, their price and their volume on the market.
 * Created by shady on 05/04/15.
 */
public class ShareTableModel extends AbstractTableModel{
    
    /** The column names. */
    String[] columnNames = {"Share ID", "Volume", "Stockprice"};

    /** The content. */
    LinkedHashSet<ShareEntry> content = new LinkedHashSet<>();

    /**
     * Instantiates a new share table model.
     *
     * @param shareEntries the share entries
     */
    public ShareTableModel(List<ShareEntry> shareEntries){
        if(shareEntries != null) {
            content.addAll(content);
        }
    }

    /**
     * Adds the row.
     *
     * @param shareEntry the share entry
     */
    public void addRow(ShareEntry shareEntry){
        if(content.contains(shareEntry)){
            content.remove(shareEntry);
        }
        content.add(shareEntry);
        fireTableDataChanged();
    }

    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    @Override
    public int getRowCount() {
        return content.size();
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
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
