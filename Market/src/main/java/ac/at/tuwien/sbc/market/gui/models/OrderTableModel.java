package ac.at.tuwien.sbc.market.gui.models;

import ac.at.tuwien.sbc.domain.entry.OrderEntry;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shady on 29/03/15.
 */
public class OrderTableModel extends AbstractTableModel{

    String[] columnNames = {"OrderID", "InvestorID", "ShareID", "Type", "Limit", "Total", "Completed", "Status"};

    List<OrderEntry> content = new ArrayList<>();

    public OrderTableModel(List<OrderEntry> orderEntries){
        if(orderEntries != null) {
            content = orderEntries;
        }else{
            content = new ArrayList<>();
        }
    }

    public void addRow(OrderEntry orderEntry){
        content.add(orderEntry);
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
            case 0: return content.get(rowIndex).getOrderID();
            case 1: return content.get(rowIndex).getInvestorID();
            case 2: return content.get(rowIndex).getShareID();
            case 3: return content.get(rowIndex).getType();
            case 4: return content.get(rowIndex).getLimit();
            case 5: return content.get(rowIndex).getNumTotal();
            case 6: return content.get(rowIndex).getNumCompleted();
            case 7: return content.get(rowIndex).getStatus();
        }
        return null;
    }
}
