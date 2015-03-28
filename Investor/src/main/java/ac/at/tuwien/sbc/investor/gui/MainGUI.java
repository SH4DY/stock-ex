package ac.at.tuwien.sbc.investor.gui;

import ac.at.tuwien.sbc.domain.entry.InvestorDepotEntry;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import ac.at.tuwien.sbc.domain.enums.OrderStatus;
import ac.at.tuwien.sbc.domain.enums.OrderType;
import ac.at.tuwien.sbc.investor.workflow.IWorkFlowObserver;
import ac.at.tuwien.sbc.investor.workflow.SpaceCoordinationService;
import ac.at.tuwien.sbc.investor.workflow.Workflow;
import org.mozartspaces.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.UUID;
import java.util.Vector;

/**
 * Created by dietl_ma on 25/03/15.
 */
public class MainGUI extends JFrame implements IWorkFlowObserver {

    private static double c = 10.0;

    private JPanel rootPanel;
    private JLabel investorLabel;
    private JTable shareTable;
    private JTable orderTable;
    private JButton addButton;
    private JTextField limitTextField;
    private JTextField numSharesTextField;
    private JComboBox typeComboBox;
    private JTextField shareTextField;

    @Autowired
    Workflow workflow;

    /**
     * The Constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(MainGUI.class);


    public MainGUI() {
        initFrame();
    }

    @PostConstruct
    private void postConstruct() {
        initShareTable();
        initOrderTable();
        initOrderForm();
    }

    private void initFrame() {
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(rootPanel);
        setVisible(true);
        setTitle("Investor App");
    }

    private void initShareTable() {

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Share ID");
        model.addColumn("#Shares");
        model.addColumn("Price");
        model.addColumn("Value");

        shareTable.setModel(model);
    }

    private void initOrderTable() {

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Order ID");
        model.addColumn("Type");
        model.addColumn("Share");
        model.addColumn("Limit");
        model.addColumn("Status");
        model.addColumn("Pending");

        orderTable.setModel(model);
    }

    private void initOrderForm() {
        typeComboBox.addItem(OrderType.BUY.toString());
        typeComboBox.addItem(OrderType.SELL.toString());

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
                            null, null);

                    workflow.addOrder(oe);
                }
            }
        });
    }

    private Integer getTableRowIndexByID(JTable table, Integer columnIndex, Object id) {

        for (int i = 0; i < table.getRowCount(); i++) {
            if (table.getValueAt(0, columnIndex).toString().equals(id.toString()))
                return i;
        }
        return null;
    }

    @Override
    public void onInvestorDepotEntryNotification(InvestorDepotEntry ide) {

        investorLabel.setText("Investor: " + ide.getInvestorID().toString() + " Budget: " + ide.getBudget().toString());
        setTitle("Investor App - " + ide.getInvestorID().toString());
    }

    @Override
    public void onOrderEntryNotification(OrderEntry oe) {
        logger.info("ORDER ENTRY OBSERVED:" + oe.getShareID().toString() + "/" + oe.getInvestorID());

        Integer rowIndex = getTableRowIndexByID(orderTable, 1, oe.getOrderID());

        if (rowIndex == null)
            rowIndex = orderTable.getRowCount();
        else
            ((DefaultTableModel)orderTable.getModel()).removeRow(rowIndex);

        Object[] newRow = new Object[]{
                oe.getOrderID().toString(),
                oe.getType().toString(),
                oe.getShareID(),
                oe.getLimit().toString(),
                oe.getStatus().toString(),
                String.valueOf(oe.getNumTotal() - oe.getNumCompleted())
        };
        ((DefaultTableModel) orderTable.getModel()).insertRow(rowIndex, newRow);

    }
}


