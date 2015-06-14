package ac.at.tuwien.sbc.market.gui.models;

import ac.at.tuwien.sbc.domain.entry.OrderEntry;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * Created by shady on 29/03/15.
 */
public class OrderTableModel extends AbstractTableModel{

    /** The column names. */
    String[] columnNames = {"OrderID", "InvestorID", "ShareID", "Type", "Prioritized", "Limit", "Total", "Completed", "Status"};

    /** The content. */
    List<OrderEntry> content = new ArrayList<>();

    /**
     * Instantiates a new order table model.
     *
     * @param orderEntries the order entries
     */
    public OrderTableModel(List<OrderEntry> orderEntries){
        if(orderEntries != null) {
            content = orderEntries;
        }else{
            content = new ArrayList<>();
        }
    }

    /**
     * Adds the row.
     *
     * @param orderEntry the order entry
     */
    public void addRow(OrderEntry orderEntry){
        content.add(orderEntry);
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
        switch (columnIndex) {
            case 0: return content.get(rowIndex).getOrderID();
            case 1: return content.get(rowIndex).getInvestorID();
            case 2: return content.get(rowIndex).getShareID();
            case 3: return content.get(rowIndex).getType();
            case 4: return content.get(rowIndex).getPrioritized() ? "Yes" : "No";
            case 5: return content.get(rowIndex).getLimit();
            case 6: return content.get(rowIndex).getNumTotal();
            case 7: return content.get(rowIndex).getNumCompleted();
            case 8: return content.get(rowIndex).getStatus();
        }
        return null;
    }

    public void insertRow(OrderEntry oe, int rowIndex){
        content.set(rowIndex, oe);
        fireTableDataChanged();
    }

    public void removeRow(int rowIndex){
        content.remove(rowIndex);
        fireTableDataChanged();
    }
}
