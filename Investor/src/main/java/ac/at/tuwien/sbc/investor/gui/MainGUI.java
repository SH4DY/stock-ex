package ac.at.tuwien.sbc.investor.gui;

import ac.at.tuwien.sbc.domain.entry.InvestorDepotEntry;
import ac.at.tuwien.sbc.domain.entry.ShareEntry;
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

/**
 * Created by dietl_ma on 25/03/15.
 */
public class MainGUI extends JFrame implements IWorkFlowObserver {

    private static double c = 10.0;

    private JPanel rootPanel;
    private JLabel investorLabel;
    private JTable shareTable;
    private JTable orderTable;
    private JComboBox comboBox1;

    @Autowired
    Workflow workflow;

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(MainGUI.class);


    public MainGUI() {
        initFrame();
    }

    @PostConstruct
    private void postConstruct() {
        initShareTable();
    }

    private void initFrame() {
        setSize(600,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(rootPanel);
        setVisible(true);
        setTitle("Investor App");
    }

    private void initShareTable() {
        String[] header = new String[] {
            "Share ID", "#Shares", "Price", "Value"
        };

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Share ID");
        model.addColumn("#Shares");
        model.addColumn("Price");
        model.addColumn("Value");

        shareTable.setModel(model);
    }

    private Integer getTableRowIndexByID(JTable table, Integer columnIndex, Object id) {

        for (int i = 0; i < table.getRowCount();i++) {
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
}
