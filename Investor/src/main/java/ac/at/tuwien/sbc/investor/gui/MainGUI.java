package ac.at.tuwien.sbc.investor.gui;

import ac.at.tuwien.sbc.domain.configuration.MarketArgsConfiguration;
import ac.at.tuwien.sbc.domain.entry.DepotEntry;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import ac.at.tuwien.sbc.domain.enums.DepotType;
import ac.at.tuwien.sbc.domain.enums.OrderStatus;
import ac.at.tuwien.sbc.domain.enums.OrderType;
import ac.at.tuwien.sbc.investor.workflow.IWorkFlowObserver;
import ac.at.tuwien.sbc.investor.workflow.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 25/03/15.
 */
public class MainGUI extends JFrame implements IWorkFlowObserver {

    /** The c. */
    private static double c = 10.0;

    /** The root panel. */
    private JPanel rootPanel;
    
    /** The investor label. */
    private JLabel investorLabel;
    
    /** The share table. */
    private JTable shareTable;
    
    /** The order table. */
    private JTable orderTable;
    
    /** The add button. */
    private JButton addButton;
    
    /** The limit text field. */
    private JTextField limitTextField;
    
    /** The num shares text field. */
    private JTextField numSharesTextField;
    
    /** The type combo box. */
    private JComboBox typeComboBox;
    
    /** The share text field. */
    private JTextField shareTextField;

    private JCheckBox prioritizedCheckBox;

    private JComboBox marketComboBox;

    private JTable marketTable;


    /** The workflow. */
    @Autowired
    Workflow workflow;

    @Autowired
    private MarketArgsConfiguration marketArgs;

    /**
     * The Constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(MainGUI.class);


    /**
     * Instantiates a new main gui.
     */
    public MainGUI() {
        initFrame();
    }

    /**
     * Post construct.
     */
    @PostConstruct
    private void postConstruct() {
        initShareTable();
        initOrderTable();
        initMarketTable();
        initOrderForm();
    }

    /**
     * Inits the frame.
     */
    private void initFrame() {
        setSize(1000, 610);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(rootPanel);
        setVisible(true);
        setTitle("Investor App");
    }

    /**
     * Inits the share table.
     */
    private void initShareTable() {

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Share ID");
        model.addColumn("#Shares");
        model.addColumn("Price");
        model.addColumn("Value");

        shareTable.setModel(model);
    }

    /**
     * Inits the order table.
     */
    private void initOrderTable() {

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Order ID");
        model.addColumn("Type");
        model.addColumn("Share");
        model.addColumn("Prioritized");
        model.addColumn("Limit");
        model.addColumn("Status");
        model.addColumn("Pending");
        model.addColumn("Action");

        orderTable.setModel(model);

        orderTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                int row = orderTable.rowAtPoint(e.getPoint());
                int column = orderTable.columnAtPoint(e.getPoint());

                if (column == 7) {
                    UUID orderId = UUID.fromString(((DefaultTableModel)(orderTable.getModel())).getValueAt(row, 0).toString());
                    workflow.deleteOrder(orderId);
                }
            }
        });
    }

    /**
     * Inits the share table.
     */
    private void initMarketTable() {

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Market");
        model.addColumn("Budget");

        marketTable.setModel(model);
    }

    /**
     * Inits the order form.
     */
    private void initOrderForm() {
        typeComboBox.addItem(OrderType.BUY.toString());
        typeComboBox.addItem(OrderType.SELL.toString());

        for (String market : (ArrayList<String>) marketArgs.getMarkets())
            marketComboBox.addItem(market);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (shareTextField.getText().length() > 0 &&
                        limitTextField.getText().length() > 0 &&
                        numSharesTextField.getText().length() > 0) {

                    OrderEntry oe = new OrderEntry(null,
                            null,
                            shareTextField.getText(),
                            OrderType.valueOf(typeComboBox.getSelectedItem().toString()),
                            Double.valueOf(limitTextField.getText()),
                            Integer.valueOf(numSharesTextField.getText()),
                            null, null,
                            prioritizedCheckBox.isSelected());

                    workflow.addOrder(oe, marketComboBox.getSelectedItem().toString());
                }
            }
        });
    }

    /**
     * Gets the table row index by id.
     *
     * @param table the table
     * @param columnIndex the column index
     * @param id the id
     * @return the table row index by id
     */
    private Integer getTableRowIndexByID(JTable table, Integer columnIndex, Object id) {

        for (int i = 0; i < table.getRowCount(); i++) {
            if (table.getValueAt(i, columnIndex).toString().equals(id.toString()))
                return i;
        }
        return null;
    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.investor.workflow.IWorkFlowObserver#onDepotEntryNotification(ac.at.tuwien.sbc.domain.entry.InvestorDepotEntry)
     */
    @Override
    public void onDepotEntryNotification(DepotEntry de, String market) {

        //set title and label
        String depotType = de.getDepotType().equals(DepotType.FOND_MANAGER) ? "Fond Manager" : "Investor";
        setTitle(depotType + " App - " + de.getId().toString() );

        //update depot info labels
        investorLabel.setText(depotType + ": " + de.getId().toString());

        updateMarketTable(de, market);
        updateShareTable(de);


    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.investor.workflow.IWorkFlowObserver#onOrderEntryNotification(ac.at.tuwien.sbc.domain.entry.OrderEntry)
     */
    @Override
    public void onOrderEntryNotification(OrderEntry oe) {

        Integer rowIndex = getTableRowIndexByID(orderTable, 0, oe.getOrderID());

        if (rowIndex == null)
            rowIndex = orderTable.getRowCount();
        else
            ((DefaultTableModel)orderTable.getModel()).removeRow(rowIndex);

        //ignore completed
        if (oe.getStatus().equals(OrderStatus.COMPLETED) ||
            oe.getStatus().equals(OrderStatus.DELETED))
            return;

        Object[] newRow = new Object[]{
                oe.getOrderID().toString(),
                oe.getType().toString(),
                oe.getShareID(),
                oe.getPrioritized() ? "Yes" : "No",
                oe.getLimit().toString(),
                oe.getStatus().toString(),
                String.valueOf(oe.getNumTotal() - oe.getNumCompleted()),
                "[delete]"};
        ((DefaultTableModel) orderTable.getModel()).insertRow(rowIndex, newRow);

    }

    /* (non-Javadoc)
     * @see ac.at.tuwien.sbc.investor.workflow.IWorkFlowObserver#onShareEntryNotification(ac.at.tuwien.sbc.domain.entry.ShareEntry)
     */
    @Override
    public void onShareEntryNotification(ShareEntry se) {
        DefaultTableModel model = ((DefaultTableModel) shareTable.getModel());
        Integer rowIndex = getTableRowIndexByID(shareTable, 0, se.getShareID());

        if (rowIndex != null) {
            model.setValueAt(se.getPrice(), rowIndex, 2);
            model.setValueAt(Integer.valueOf((String)(model.getValueAt(rowIndex, 1)))*se.getPrice(), rowIndex, 3);
        }
    }

    /**
     * Update share table
     * @param de
     */
    private void updateShareTable(DepotEntry de) {

        DefaultTableModel model = ((DefaultTableModel) shareTable.getModel());
        for (String shareId : de.getShareDepot().keySet()) {

            Integer numShares = de.getShareDepot().get(shareId);
            Integer rowIndex = getTableRowIndexByID(shareTable, 0, shareId);

            if (rowIndex == null) {
                rowIndex = shareTable.getRowCount();

                Object[] newRow = new Object[]{
                        shareId.toString(),
                        numShares.toString(),
                        0,
                        0
                };
                model.insertRow(rowIndex, newRow);
            }
            else {
                if (numShares <= 0) {
                    model.removeRow(rowIndex);
                    continue;
                }
                else {
                    Double value = Double.parseDouble((model.getValueAt(rowIndex, 2).toString()))*numShares;
                    model.setValueAt(shareId.toString(), rowIndex, 0);
                    model.setValueAt(numShares.toString(), rowIndex, 1);
                    model.setValueAt(value.toString(), rowIndex, 3);
                }
            }
        }
    }

    /**
     * Update market table
     * @param de
     * @param market
     */
    private void updateMarketTable(DepotEntry de, String market) {

        DefaultTableModel model = ((DefaultTableModel) marketTable.getModel());
        Integer rowIndex = getTableRowIndexByID(marketTable, 0, market);

        if (rowIndex == null)
            rowIndex = marketTable.getRowCount();
        else
            ((DefaultTableModel)marketTable.getModel()).removeRow(rowIndex);


        Object[] newRow = new Object[]{
                market,
                de.getBudget().toString()};
        ((DefaultTableModel) marketTable.getModel()).insertRow(rowIndex, newRow);
    }
}


