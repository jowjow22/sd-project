package client.views;

import enums.Operations;
import helpers.singletons.IOConnection;
import helpers.singletons.Json;
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

        goToLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                LoginUser login = new LoginUser();
                login.setVisible(true);
            }
        });

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String userName = name.getText();
                String userEmail = email.getText();
                String userPassword = new String(password.getPassword());


                CandidateSignUpRequest candidateLogin = new CandidateSignUpRequest(userName, userEmail, userPassword);

                Request request = new Request<>(Operations.SIGNUP_CANDIDATE, candidateLogin);

                IOConnection io = IOConnection.getInstance();
                Json jsonParser = Json.getInstance();

                String json = jsonParser.toJson(request);
                System.out.println(json);
                io.send(json);
                Response<CandidateLoginResponse> receivedMessage = null;
                try {
                    receivedMessage = jsonParser.fromJson(io.receive(), Response.class);
                } catch (IOException err) {
                    throw new RuntimeException(err);
                }

                CandidateLoginResponse candidateLoginResponse = receivedMessage.data(CandidateLoginResponse.class);
                System.out.println("Token: " + candidateLoginResponse.token());

                dispose();
            }
        });

        returnButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                SignOptions signOptions = new SignOptions();
                signOptions.pack();
                signOptions.setVisible(true);
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        SignUpCandidate dialog = new SignUpCandidate();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
