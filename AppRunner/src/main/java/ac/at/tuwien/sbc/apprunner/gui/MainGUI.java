package ac.at.tuwien.sbc.apprunner.gui;

import org.apache.commons.lang.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * Created by dietl_ma on 14/04/15.
 */
public class MainGUI extends JFrame {

    /** The process lists. */
    ArrayList<ArrayList<Process>> processLists;

    /** The market processes. */
    private ArrayList<Process> marketProcesses;
    
    /** The investor processes. */
    private ArrayList<Process> investorProcesses;
    
    /** The broker processes. */
    private ArrayList<Process> brokerProcesses;
    
    /** The company processes. */
    private ArrayList<Process> companyProcesses;
    
    /** The market agent processes. */
    private ArrayList<Process> marketAgentProcesses;

    /** The root panel. */
    private JPanel rootPanel;
    
    /** The space radio button. */
    private JRadioButton spaceRadioButton;
    
    /** The amqp radio button. */
    private JRadioButton amqpRadioButton;
    
    /** The investor id field. */
    private JTextField investorIDField;
    
    /** The investor budget field. */
    private JTextField investorBudgetField;
    
    /** The run broker button. */
    private JButton runBrokerButton;
    
    /** The broker id field. */
    private JTextField brokerIDField;
    
    /** The run investor button. */
    private JButton runInvestorButton;
    
    /** The run market button. */
    private JButton runMarketButton;
    
    /** The company num shares field. */
    private JTextField companyNumSharesField;
    
    /** The company price field. */
    private JTextField companyPriceField;
    
    /** The run company button. */
    private JButton runCompanyButton;
    
    /** The company id field. */
    private JTextField companyIDField;
    
    /** The run market agent button. */
    private JButton runMarketAgentButton;
    
    /** The num market processes. */
    private JLabel numMarketProcesses;
    
    /** The num investor processes. */
    private JLabel numInvestorProcesses;
    
    /** The num broker processes. */
    private JLabel numBrokerProcesses;
    
    /** The num company processes. */
    private JLabel numCompanyProcesses;
    
    /** The num market agent processes. */
    private JLabel numMarketAgentProcesses;
    
    /** The close all button. */
    private JButton closeAllButton;
    
    /** The implementation button group. */
    private ButtonGroup implementationButtonGroup;
    
    /** The run action listener. */
    private RunActionListener runActionListener;

    /**
     * Instantiates a new main gui.
     */
    public MainGUI(){
        setSize(850,290);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(rootPanel);
        setTitle("App Runner");
        setVisible(true);

        initImplementationButtonGroup();
        initRunButtons();

        marketProcesses = new ArrayList<Process>();
        investorProcesses = new ArrayList<Process>();
        brokerProcesses = new ArrayList<Process>();
        companyProcesses = new ArrayList<Process>();
        marketAgentProcesses = new ArrayList<Process>();

        processLists = new ArrayList<ArrayList<Process>>();
        processLists.add(marketProcesses);
        processLists.add(investorProcesses);
        processLists.add(brokerProcesses);
        processLists.add(companyProcesses);
        processLists.add(marketAgentProcesses);

        //destroy processes
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                closeAllProcesses();
                super.windowClosing(e);
            }
        });

        closeAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeAllProcesses();
            }
        });

    }

    /**
     * Inits the implementation button group.
     */
    private void initImplementationButtonGroup() {
        implementationButtonGroup = new ButtonGroup();
        spaceRadioButton.setActionCommand("space");
        amqpRadioButton.setActionCommand("amqp");
        implementationButtonGroup.add(spaceRadioButton);
        implementationButtonGroup.add(amqpRadioButton);
    }

    /**
     * Inits the run buttons.
     */
    private void initRunButtons() {

        runMarketButton.addActionListener(new RunActionListener());
        runInvestorButton.addActionListener(new RunActionListener());
        runBrokerButton.addActionListener(new RunActionListener());
        runCompanyButton.addActionListener(new RunActionListener());
        runMarketAgentButton.addActionListener(new RunActionListener());
    }

    /**
     * Check processes.
     */
    @Scheduled(fixedDelay = 2000)
    private void checkProcesses() {
        numMarketProcesses.setText(String.valueOf(marketProcesses.size()));
        numInvestorProcesses.setText(String.valueOf(investorProcesses.size()));
        numBrokerProcesses.setText(String.valueOf(brokerProcesses.size()));
        numCompanyProcesses.setText(String.valueOf(companyProcesses.size()));
        numMarketAgentProcesses.setText(String.valueOf(marketAgentProcesses.size()));
    }

    /**
     * Close all processes.
     */
    private void closeAllProcesses() {
        for (ArrayList<Process> processes : processLists) {
            for (Process p : processes)
                p.destroy();

            processes.clear();
        }
    }

    /**
     * The listener interface for receiving runAction events.
     * The class that is interested in processing a runAction
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's <code>addRunActionListener<code> method. When
     * the runAction event occurs, that object's appropriate
     * method is invoked.
     *
     * @see RunActionEvent
     */
    public class RunActionListener implements ActionListener {

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {

            ArrayList<String> command = new ArrayList<String>();

            command.add("java -jar");
            ArrayList<Process> processList = null;

            switch (e.getActionCommand()) {
                case "runMarket":
                    command.add("Market/target/market-1.0-SNAPSHOT.jar");
                    processList = marketProcesses;
                    break;
                case "runInvestor":
                    command.add("Investor/target/investor-1.0-SNAPSHOT.jar");
                    command.add("--id=" + investorIDField.getText());
                    command.add("--budget=" + investorBudgetField.getText());
                    processList = investorProcesses;
                    break;
                case "runBroker":
                    command.add("Broker/target/broker-1.0-SNAPSHOT.jar");
                    command.add("--id=" + brokerIDField.getText());
                    processList = brokerProcesses;
                    break;
                case "runCompany":
                    command.add("Company/target/company-1.0-SNAPSHOT.jar");
                    command.add("--id=" + companyIDField.getText());
                    command.add("--numShares=" + companyNumSharesField.getText());
                    command.add("--initPrice=" + companyPriceField.getText());
                    processList = companyProcesses;
                    break;
                case "runMarketAgent":
                    command.add("MarketAgent/target/marketagent-1.0-SNAPSHOT.jar");
                    processList = marketAgentProcesses;
                    break;
            }

            command.add("--spring.profiles.active=" + implementationButtonGroup.getSelection().getActionCommand());

            try {
                System.out.println("TRY TO RUN: " + StringUtils.join(command, " "));
                Process p = Runtime.getRuntime().exec(StringUtils.join(command, " "));
                processList.add(p);

            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }
    }
}
