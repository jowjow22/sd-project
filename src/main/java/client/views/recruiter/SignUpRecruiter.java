package client.views.recruiter;

import client.views.SignOptions;
import enums.Operations;
import enums.Statuses;
import helpers.singletons.IOConnection;
import records.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class SignUpRecruiter extends JDialog {
    private JPanel contentPane;
    private JButton sendButton;
    private JButton returnButton;
    private JPasswordField password;

    private JTextField name;
    private JFormattedTextField email;
    private JButton goToLogin;
    private JTextField industry;
    private JTextField description;

    public SignUpRecruiter() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(sendButton);
        setMinimumSize(new Dimension(400, 400));
        goToLogin.setBorder(BorderFactory.createEmptyBorder());
        goToLogin.setFocusPainted(false);
        goToLogin.setContentAreaFilled(false);
        goToLogin.setBackground(new Color(0,0,0,0));

        Font font = new Font("SansSerif", Font.BOLD, 12);
        goToLogin.setFont(font);
        goToLogin.setForeground(Color.BLUE);

        goToLogin.addActionListener(e -> {
            dispose();
            LoginRecruiter login = new LoginRecruiter();
            login.setVisible(true);
        });

        sendButton.addActionListener(e -> {
            String userName = name.getText();
            String userEmail = email.getText();
            String userPassword = new String(password.getPassword());
            String userIndustry = industry.getText();
            String userDescription = description.getText();


            RecruiterSignUpAndUpdateRequest recruiterSignUp = new RecruiterSignUpAndUpdateRequest(userName, userEmail, userPassword, userIndustry, userDescription);

            Request<?> request = new Request<>(Operations.SIGNUP_RECRUITER, recruiterSignUp);

            IOConnection io = IOConnection.getInstance();

            io.send(request);
            Response<?> response;
            try {
                response = io.receive(RecruiterLoginResponse.class);
            } catch (IOException err) {
                throw new RuntimeException(err);
            }

            if (response.status() == Statuses.SUCCESS) {
                dispose();
                JOptionPane.showMessageDialog(null, "Account created successfully");
                LoginRecruiter login = new LoginRecruiter();
                login.setVisible(true);
            } else if (response.status() == Statuses.INVALID_EMAIL) {
                JOptionPane.showMessageDialog(null, "Email already in use");
            } else {
                JOptionPane.showMessageDialog(null, "Account creation failed");
            }
        });

        returnButton.addActionListener(e -> {
            dispose();
            SignOptions signOptions = new SignOptions();
            signOptions.pack();
            signOptions.setVisible(true);
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }


    private void onCancel() {

        dispose();
    }

    public static void main(String[] args) {
        SignUpRecruiter dialog = new SignUpRecruiter();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
