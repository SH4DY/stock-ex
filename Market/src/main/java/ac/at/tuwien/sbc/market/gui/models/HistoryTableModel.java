package ac.at.tuwien.sbc.market.gui.models;

import ac.at.tuwien.sbc.domain.entry.TransactionEntry;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * TableModel to visualize the HistoryTable on the Main GUI
 * which displays all trades happened in the given space.
 * Created by shady on 05/04/15.
 */
public class HistoryTableModel extends AbstractTableModel{

    String[] columnNames = {"Transaction ID", "Broker ID", "Buyer ID", "Seller ID", "Share ID", "SellOrder ID", "BuyOrder ID", "Price", "Volume", "Total", "Provision"};

    List<TransactionEntry> content = new ArrayList<>();

    public HistoryTableModel(List<TransactionEntry> transactionEntries){
        if(transactionEntries != null) {
            content = transactionEntries;
        }else{
            content = new ArrayList<>();
        }
    }

    public void addRow(TransactionEntry transactionEntry){
        content.add(transactionEntry);
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
            case 0: return content.get(rowIndex).getTransactionID();
            case 1: return content.get(rowIndex).getBrokerID();
            case 2: return content.get(rowIndex).getBuyerID();
            case 3: return content.get(rowIndex).getSellerID();
            case 4: return content.get(rowIndex).getShareID();
            case 5: return content.get(rowIndex).getSellOrderID();
            case 6: return content.get(rowIndex).getBuyOrderID();
            case 7: return content.get(rowIndex).getPrice();
            case 8: return content.get(rowIndex).getNumShares();
            case 9: return content.get(rowIndex).getSumPrice();
            case 10: return content.get(rowIndex).getProvision();
        }
        return null;
    }
}
