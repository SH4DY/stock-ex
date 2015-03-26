package ac.at.tuwien.sbc.market.gui;

import javax.swing.*;

/**
 * Created by dietl_ma on 25/03/15.
 */
public class MainGUI extends JFrame{
    private JLabel titleLabel;
    private JPanel rootPanel;

    public MainGUI() {
        setSize(600,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(rootPanel);
        setVisible(true);
    }
}
