package ac.at.tuwien.sbc.market.gui;

import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.TransactionEntry;
import ac.at.tuwien.sbc.market.gui.models.HistoryTableModel;
import ac.at.tuwien.sbc.market.gui.models.OrderTableModel;
import ac.at.tuwien.sbc.market.workflow.IMarketObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**Entry point GUI for the Market application.
 * Created by dietl_ma on 25/03/15.
 */
public class MainGUI extends JFrame implements IMarketObserver{
    private JLabel titleLabel;
    private JPanel rootPanel;
    private JTable overviewTable;
    private JTable historyTable;
    private JTable orderTable;
    private JScrollPane overviewPanel;
    private JScrollPane historyPanel;
    private JScrollPane orderPanel;

    private static final Logger logger = LoggerFactory.getLogger(MainGUI.class);

    private OrderTableModel orderTableModel;
    private HistoryTableModel historyTableModel;

    public MainGUI(){
        setSize(1200,1200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(rootPanel);

        initOverviewPanel();
        initHistoryPanel();
        initOrderPanel();
        setVisible(true);
    }

    private void initOverviewPanel(){
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("ShareID");
        tableModel.addColumn("Volume");
        tableModel.addColumn("Stockprice");

        overviewTable.setModel(tableModel);

        //This is just a hack to make the table not-editable
        overviewTable.setEnabled(false);
    }

    private void initHistoryPanel(){
        historyTableModel = new HistoryTableModel(null);
        historyTable.setModel(historyTableModel);

        //This is just a hack to make the table not-editable
        overviewTable.setEnabled(false);
    }

    private void initOrderPanel(){
        orderTableModel = new OrderTableModel(null);
        orderTable.setModel(orderTableModel);
    }

    @Override
    public void onStockpriceChanged() {

    }

    @Override
    public void onTransactionAdded(TransactionEntry transactionEntry) {
        if(transactionEntry != null) {
            logger.debug("Main GUI notified of transactionEntry");
            historyTableModel.addRow(transactionEntry);
        }
    }

    @Override
    public void onOrderAdded(OrderEntry orderEntry) {
        if(orderEntry != null) {
            logger.debug("Main GUI notified of orderEntry");
            orderTableModel.addRow(orderEntry);
        }
    }

}
