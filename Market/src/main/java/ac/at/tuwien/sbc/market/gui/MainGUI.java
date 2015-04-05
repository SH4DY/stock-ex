package ac.at.tuwien.sbc.market.gui;

import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import ac.at.tuwien.sbc.domain.entry.TransactionEntry;
import ac.at.tuwien.sbc.market.gui.models.HistoryTableModel;
import ac.at.tuwien.sbc.market.gui.models.OrderTableModel;
import ac.at.tuwien.sbc.market.gui.models.ShareTableModel;
import ac.at.tuwien.sbc.market.workflow.IMarketObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**Entry point GUI for the Market application.
 * Created by dietl_ma on 25/03/15.
 */
public class MainGUI extends JFrame implements IMarketObserver{
    private JLabel titleLabel;
    private JPanel rootPanel;
    private JTable shareTable; //Shows shares, volume and price on the market
    private JTable historyTable; //Shows history of transactions
    private JTable orderTable; //Shows all orders and their state
    private JScrollPane sharePanel;
    private JScrollPane historyPanel;
    private JScrollPane orderPanel;

    private static final Logger logger = LoggerFactory.getLogger(MainGUI.class);

    private OrderTableModel orderTableModel;
    private HistoryTableModel historyTableModel;
    private ShareTableModel shareTableModel;

    public MainGUI(){
        setSize(1200,1200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(rootPanel);

        initSharePanel();
        initHistoryPanel();
        initOrderPanel();
        setVisible(true);
    }

    private void initSharePanel(){
        shareTableModel = new ShareTableModel(null);
        shareTable.setModel(shareTableModel);

        //This is just a hack to make the table not-editable
        shareTable.setEnabled(false);
    }

    private void initHistoryPanel(){
        historyTableModel = new HistoryTableModel(null);
        historyTable.setModel(historyTableModel);

        //This is just a hack to make the table not-editable
        shareTable.setEnabled(false);
    }

    private void initOrderPanel(){
        orderTableModel = new OrderTableModel(null);
        orderTable.setModel(orderTableModel);
    }

    @Override
    public void onStockpriceChanged() {
        //TODO
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

    @Override
    public void onShareAdded(ShareEntry shareEntry) {
        if(shareEntry != null) {
            logger.debug("Main GUI notified of shareEntry");

            if(shareEntry.)
            shareTableModel.addRow(shareEntry);
        }
    }

}
