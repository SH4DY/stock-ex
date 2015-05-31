package ac.at.tuwien.sbc.market.gui;

import ac.at.tuwien.sbc.domain.configuration.MarketArgsConfiguration;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import ac.at.tuwien.sbc.domain.entry.TransactionEntry;
import ac.at.tuwien.sbc.market.gui.models.HistoryTableModel;
import ac.at.tuwien.sbc.market.gui.models.OrderTableModel;
import ac.at.tuwien.sbc.market.gui.models.ShareTableModel;
import ac.at.tuwien.sbc.market.workflow.IMarketObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.swing.*;


/**Entry point GUI for the Market application.
 * Created by dietl_ma on 25/03/15.
 */
public class MainGUI extends JFrame implements IMarketObserver{
    
    /** The title label. */
    private JLabel titleLabel;
    
    /** The root panel. */
    private JPanel rootPanel;
    
    /** The share table. */
    private JTable shareTable; //Shows shares, volume and price on the market
    
    /** The history table. */
    private JTable historyTable; //Shows history of transactions
    
    /** The order table. */
    private JTable orderTable; //Shows all orders and their state
    
    /** The share panel. */
    private JScrollPane sharePanel;
    
    /** The history panel. */
    private JScrollPane historyPanel;
    
    /** The order panel. */
    private JScrollPane orderPanel;

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(MainGUI.class);

    /** The order table model. */
    private OrderTableModel orderTableModel;
    
    /** The history table model. */
    private HistoryTableModel historyTableModel;
    
    /** The share table model. */
    private ShareTableModel shareTableModel;

    @Autowired
    private MarketArgsConfiguration marketArgs;

    /**
     * Instantiates a new main gui.
     */
    public MainGUI(){
        setSize(1200,1200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(rootPanel);


        initSharePanel();
        initHistoryPanel();
        initOrderPanel();
        setVisible(true);
    }

    @PostConstruct
    public void onPostConstruct() {
        setTitle("Market App - " + marketArgs.getMarkets().get(0));
    }
    /**
     * Inits the share panel.
     */
    private void initSharePanel(){
        shareTableModel = new ShareTableModel(null);
        shareTable.setModel(shareTableModel);

        //This is just a hack to make the table not-editable
        shareTable.setEnabled(false);
    }

    /**
     * Inits the history panel.
     */
    private void initHistoryPanel(){
        historyTableModel = new HistoryTableModel(null);
        historyTable.setModel(historyTableModel);

        //This is just a hack to make the table not-editable
        shareTable.setEnabled(false);
    }

    /**
     * Inits the order panel.
     */
    private void initOrderPanel(){
        orderTableModel = new OrderTableModel(null);
        orderTable.setModel(orderTableModel);
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.market.workflow.IMarketObserver#onStockpriceChanged()
     */
    @Override
    public void onStockpriceChanged() {
        //TODO
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.market.workflow.IMarketObserver#onTransactionAdded(ac.at.tuwien.sbc.domain.entry.TransactionEntry)
     */
    @Override
    public void onTransactionAdded(TransactionEntry transactionEntry) {
        if(transactionEntry != null) {
            logger.debug("Main GUI notified of transactionEntry");
            historyTableModel.addRow(transactionEntry);
        }
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.market.workflow.IMarketObserver#onOrderAdded(ac.at.tuwien.sbc.domain.entry.OrderEntry)
     */
    @Override
    public void onOrderAdded(OrderEntry orderEntry) {
        if(orderEntry != null) {
            logger.debug("Main GUI notified of orderEntry");

            Integer row = getRowIdInOrderTable(orderEntry);
            if(row == null){
                orderTableModel.addRow(orderEntry);
            }else{
                orderTableModel.insertRow(orderEntry, row);
            }
        }
    }

    private Integer getRowIdInOrderTable(OrderEntry orderEntry) {
        int rows = orderTable.getRowCount();
        Integer row = null;

        for(int i = 0; i < rows; i++){
            if(orderTable.getValueAt(i, 0).equals(orderEntry.getOrderID())){
                row = i;
                break; //we found an occurence of this particular order, break and return
            }
        }
        return row;
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.market.workflow.IMarketObserver#onShareAdded(ac.at.tuwien.sbc.domain.entry.ShareEntry)
     */
    @Override
    public void onShareAdded(ShareEntry shareEntry) {
        if(shareEntry != null) {
            logger.debug("Main GUI notified of shareEntry");

            shareTableModel.addRow(shareEntry);
        }
    }

}
