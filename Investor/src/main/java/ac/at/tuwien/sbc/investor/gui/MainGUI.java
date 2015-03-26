package ac.at.tuwien.sbc.investor.gui;

import ac.at.tuwien.sbc.domain.entry.ShareEntry;
import org.mozartspaces.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by dietl_ma on 25/03/15.
 */
public class MainGUI extends JFrame {

    private JPanel rootPanel;
    private JLabel titleLabel;
    private JButton button1;

    @Autowired
    @Qualifier("defaultContainer")
    ContainerReference cRef;

    @Autowired
    Capi capi;

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(MainGUI.class);


    public MainGUI() {
        setSize(600,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(rootPanel);
        setVisible(true);

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ShareEntry s = new ShareEntry("GOOOOOO", 23, 23.0);


                try {
                    Entry entry = new Entry(s);
                    capi.write(cRef, MzsConstants.RequestTimeout.TRY_ONCE, null, entry);
                } catch (MzsCoreException ex) {
                    ex.printStackTrace();
                }

                System.out.println("WRITTEN");
            }
        });
    }



}
