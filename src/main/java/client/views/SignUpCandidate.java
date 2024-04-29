package client.views;

import enums.Operations;
import enums.Statuses;
import helpers.singletons.IOConnection;
import records.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class SignUpCandidate extends JDialog {
    private JPanel contentPane;
    private JButton sendButton;
    private JButton returnButton;
    private JPasswordField password;

    private JTextField name;
    private JFormattedTextField email;
    private JButton goToLogin;

    public SignUpCandidate() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(sendButton);
        setMinimumSize(new Dimension(400, 400));
        goToLogin.setBorder(BorderFactory.createEmptyBorder());
        goToLogin.setFocusPainted(false);
        goToLogin.setContentAreaFilled(false);
        goToLogin.setBackground(new java.awt.Color(0,0,0,0));

        Font font = new Font("SansSerif", Font.BOLD, 12);
        goToLogin.setFont(font);
        goToLogin.setForeground(Color.BLUE);

        goToLogin.addActionListener(e -> {
            dispose();
            LoginUser login = new LoginUser();
            login.setVisible(true);
        });

        sendButton.addActionListener(e -> {
            String userName = name.getText();
            String userEmail = email.getText();
            String userPassword = new String(password.getPassword());


            CandidateSignUpRequest candidateLogin = new CandidateSignUpRequest(userName, userEmail, userPassword);

            Request<?> request = new Request<>(Operations.SIGNUP_CANDIDATE, candidateLogin);

            IOConnection io = IOConnection.getInstance();

            io.send(request);
            Response<CandidateLoginResponse> response;
            try {
                response = io.receive(CandidateLoginResponse.class);
            } catch (IOException err) {
                throw new RuntimeException(err);
            }

            if (response.status() == Statuses.SUCCESS) {
                JOptionPane.showMessageDialog(null, "Account created successfully");
            } else {
                JOptionPane.showMessageDialog(null, "Account creation failed");
            }

            LoginUser login = new LoginUser();
            login.setVisible(true);
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
        SignUpCandidate dialog = new SignUpCandidate();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
