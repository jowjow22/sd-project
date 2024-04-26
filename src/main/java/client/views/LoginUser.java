package client.views;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginUser extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;

    private JTextField email;
    private JPasswordField password;
    @Getter
    private String userEmail;
    @Getter
    private String userPassword;

    public LoginUser() {
        setContentPane(contentPane);
        setModal(true);
        setMinimumSize(new Dimension(400, 400));
        getRootPane().setDefaultButton(buttonOK);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        buttonCancel.addActionListener(e -> {
            dispose();
        });

        buttonOK.addActionListener(e -> {
            userEmail = email.getText();
            userPassword = new String(password.getPassword());

            dispose();
        });
    }

    public static void main(String[] args) {
        LoginUser dialog = new LoginUser();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
