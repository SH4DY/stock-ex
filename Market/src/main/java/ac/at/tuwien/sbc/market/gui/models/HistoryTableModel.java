package ac.at.tuwien.sbc.market.gui.models;

import ac.at.tuwien.sbc.domain.entry.TransactionEntry;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * TableModel to visualize the HistoryTable on the Main GUI
 * which displays all trades happened in the given space.
 * Created by shady on 05/04/15.
 */
public class HistoryTableModel extends AbstractTableModel{

    /** The column names. */
    String[] columnNames = {"Transaction ID", "Broker ID", "Buyer ID", "Seller ID", "Share ID", "SellOrder ID", "BuyOrder ID", "Price", "Volume", "Total", "Provision"};

    /** The content. */
    List<TransactionEntry> content = new ArrayList<>();

    /**
     * Instantiates a new history table model.
     *
     * @param transactionEntries the transaction entries
     */
    public HistoryTableModel(List<TransactionEntry> transactionEntries){
        if(transactionEntries != null) {
            content = transactionEntries;
        }else{
            content = new ArrayList<>();
        }
    }

    /**
     * Adds the row.
     *
     * @param transactionEntry the transaction entry
     */
    public void addRow(TransactionEntry transactionEntry){
        content.add(transactionEntry);
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
