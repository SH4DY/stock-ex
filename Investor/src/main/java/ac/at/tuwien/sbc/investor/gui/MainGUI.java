package ac.at.tuwien.sbc.investor.gui;

import ac.at.tuwien.sbc.domain.entry.InvestorDepotEntry;
import ac.at.tuwien.sbc.domain.entry.OrderEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
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
import java.util.UUID;

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
        model.addColumn("Action");

        orderTable.setModel(model);

        orderTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                int row = orderTable.rowAtPoint(e.getPoint());
                int column = orderTable.columnAtPoint(e.getPoint());

                if (column == 6) {
                    UUID orderId = UUID.fromString(((DefaultTableModel)(orderTable.getModel())).getValueAt(row, 0).toString());
                    workflow.deleteOrder(orderId);
                }
            }
        });
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
            if (table.getValueAt(i, columnIndex).toString().equals(id.toString()))
                return i;
        }
        return null;
    }

    @Override
    public void onInvestorDepotEntryNotification(InvestorDepotEntry ide) {

        //set title and label
        investorLabel.setText("Investor: " + ide.getInvestorID().toString() + " Budget: " + ide.getBudget().toString());
        setTitle("Investor App - " + ide.getInvestorID().toString());

        //update share table
        DefaultTableModel model = ((DefaultTableModel) shareTable.getModel());
        for (String shareId : ide.getShareDepot().keySet()) {

            Integer numShares = ide.getShareDepot().get(shareId);
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
                oe.getLimit().toString(),
                oe.getStatus().toString(),
                String.valueOf(oe.getNumTotal() - oe.getNumCompleted()),
                "[delete]"};
        ((DefaultTableModel) orderTable.getModel()).insertRow(rowIndex, newRow);

    }

    @Override
    public void onShareEntryNotification(ShareEntry se) {
        DefaultTableModel model = ((DefaultTableModel) shareTable.getModel());
        Integer rowIndex = getTableRowIndexByID(shareTable, 0, se.getShareID());

        if (rowIndex != null) {
            model.setValueAt(se.getPrice(), rowIndex, 2);
            model.setValueAt(Integer.valueOf((String)(model.getValueAt(rowIndex, 1)))*se.getPrice(), rowIndex, 3);
        }
    }
}


