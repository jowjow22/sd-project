package client.views;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class StartConnection extends JDialog {

    @Getter
    private String serverIp;

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField ip;

    public StartConnection() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setMinimumSize(new Dimension(500, 500));
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);


        buttonCancel.addActionListener(e -> {
                dispose();
        });
        buttonOK.addActionListener(e -> {
                serverIp = ip.getText();
                dispose();
        });
        setVisible(true);
    }
}
