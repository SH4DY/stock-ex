package ac.at.tuwien.sbc.apprunner.gui;

import org.apache.commons.lang.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by dietl_ma on 14/04/15.
 */
public class MainGUI extends JFrame {

    ArrayList<ArrayList<Process>> processLists;

    private ArrayList<Process> marketProcesses;
    private ArrayList<Process> investorProcesses;
    private ArrayList<Process> brokerProcesses;
    private ArrayList<Process> companyProcesses;
    private ArrayList<Process> marketAgentProcesses;

    private JPanel rootPanel;
    private JRadioButton spaceRadioButton;
    private JRadioButton amqpRadioButton;
    private JTextField investorIDField;
    private JTextField investorBudgetField;
    private JButton runBrokerButton;
    private JTextField brokerIDField;
    private JButton runInvestorButton;
    private JButton runMarketButton;
    private JTextField companyNumSharesField;
    private JTextField companyPriceField;
    private JButton runCompanyButton;
    private JTextField companyIDField;
    private JButton runMarketAgentButton;
    private JLabel numMarketProcesses;
    private JLabel numInvestorProcesses;
    private JLabel numBrokerProcesses;
    private JLabel numCompanyProcesses;
    private JLabel numMarketAgentProcesses;
    private JButton closeAllButton;
    private ButtonGroup implementationButtonGroup;
    private RunActionListener runActionListener;

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

    private void initImplementationButtonGroup() {
        implementationButtonGroup = new ButtonGroup();
        spaceRadioButton.setActionCommand("space");
        amqpRadioButton.setActionCommand("amqp");
        implementationButtonGroup.add(spaceRadioButton);
        implementationButtonGroup.add(amqpRadioButton);
    }

    private void initRunButtons() {

        runMarketButton.addActionListener(new RunActionListener());
        runInvestorButton.addActionListener(new RunActionListener());
        runBrokerButton.addActionListener(new RunActionListener());
        runCompanyButton.addActionListener(new RunActionListener());
        runMarketAgentButton.addActionListener(new RunActionListener());
    }

    @Scheduled(fixedDelay = 2000)
    private void checkProcesses() {
        numMarketProcesses.setText(String.valueOf(marketProcesses.size()));
        numInvestorProcesses.setText(String.valueOf(investorProcesses.size()));
        numBrokerProcesses.setText(String.valueOf(brokerProcesses.size()));
        numCompanyProcesses.setText(String.valueOf(companyProcesses.size()));
        numMarketAgentProcesses.setText(String.valueOf(marketAgentProcesses.size()));
    }

    private void closeAllProcesses() {
        for (ArrayList<Process> processes : processLists) {
            for (Process p : processes)
                p.destroy();

            processes.clear();
        }
    }

    public class RunActionListener implements ActionListener {

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
