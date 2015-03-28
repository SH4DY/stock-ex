package ac.at.tuwien.sbc.market.gui;

import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.TransactionEntry;
import ac.at.tuwien.sbc.market.workflow.IMarketObserver;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
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

        tableModel.addRow(new String[]{"GOOG", "10", "100"});
        tableModel.addRow(new String[]{"MSFT", "10", "100"});
        tableModel.addRow(new String[]{"YAHO", "5", "700"});
        tableModel.addRow(new String[]{"GOOG", "10", "100"});
        tableModel.addRow(new String[]{"MSFT", "10", "100"});
        tableModel.addRow(new String[]{"YAHO", "5", "700"});
        tableModel.addRow(new String[]{"GOOG", "10", "100"});
        tableModel.addRow(new String[]{"MSFT", "10", "100"});
        tableModel.addRow(new String[]{"YAHO", "5", "700"});
        tableModel.addRow(new String[]{"GOOG", "10", "100"});
        tableModel.addRow(new String[]{"MSFT", "10", "100"});
        tableModel.addRow(new String[]{"YAHO", "5", "700"});
        tableModel.addRow(new String[]{"GOOG", "10", "100"});
        tableModel.addRow(new String[]{"MSFT", "10", "100"});
        tableModel.addRow(new String[]{"YAHO", "5", "700"});
        overviewTable.setModel(tableModel);

        //This is just a hack to make the table not-editable
        overviewTable.setEnabled(false);
    }

    private void initHistoryPanel(){
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Transaction ID");
        tableModel.addColumn("BrokerID");
        tableModel.addColumn("BuyerID");
        tableModel.addColumn("SellerID");
        tableModel.addColumn("ShareID");
        tableModel.addColumn("SellOrderID");
        tableModel.addColumn("buyOrderID");
        tableModel.addColumn("price");
        tableModel.addColumn("Volume");
        tableModel.addColumn("Total");
        tableModel.addColumn("Provision");

        historyTable.setModel(tableModel);

        //This is just a hack to make the table not-editable
        overviewTable.setEnabled(false);
    }

    private void initOrderPanel(){
        DefaultTableModel tableModel = new DefaultTableModel();

        tableModel.addColumn("OrderID");
        tableModel.addColumn("InvestorID");
        tableModel.addColumn("ShareID");
        tableModel.addColumn("Type");
        tableModel.addColumn("Limit");
        tableModel.addColumn("Total");
        tableModel.addColumn("Completed");
        tableModel.addColumn("Status");

        orderTable.setModel(tableModel);
    }

    @Override
    public void onStockpriceChanged() {

    }

    @Override
    public void onTransactionAdded(TransactionEntry transactionEntry) {

    }

    @Override
    public void onOrderAdded(OrderEntry orderEntry) {
        //TODO display added order
    }

}
