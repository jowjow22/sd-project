package client;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class StartConnection extends JDialog {

    @Getter
    private String serverIp;

    @Getter
    private int serverPort;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField ip;
    private JTextField port;

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
                System.out.println("onOK");
                serverIp = ip.getText();
                serverPort = Integer.parseInt(port.getText());
                dispose();
        });
        setVisible(true);
    }
}
