package client.views;

import client.store.CandidateStore;
import enums.Operations;
import enums.Statuses;
import helpers.singletons.IOConnection;
import models.Candidate;
import records.CandidateLoginRequest;
import records.CandidateLoginResponse;
import records.Request;
import records.Response;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class LoginUser extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;

    private JTextField email;
    private JPasswordField password;
    private String userEmail;
    private String userPassword;

    public LoginUser() {
        setContentPane(contentPane);
        setModal(true);
        setMinimumSize(new Dimension(400, 400));
        getRootPane().setDefaultButton(buttonOK);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        buttonCancel.addActionListener(e -> {
            dispose();
            SignUpCandidate signUpCandidate = new SignUpCandidate();
            signUpCandidate.setVisible(true);
        });

        buttonOK.addActionListener(e -> {
            userEmail = email.getText();
            userPassword = new String(password.getPassword());



            IOConnection io = IOConnection.getInstance();

            CandidateLoginRequest candidateLogin = new CandidateLoginRequest(userEmail, userPassword);
            Request<CandidateLoginRequest> request = new Request<>(Operations.LOGIN_CANDIDATE,candidateLogin);
            io.send(request);
            Response<CandidateLoginResponse> receivedMessage;
            try {
                receivedMessage = io.receive(CandidateLoginResponse.class);

                if (receivedMessage.status().equals(Statuses.INVALID_LOGIN) || receivedMessage.status().equals(Statuses.INVALID_FIELD)){
                    JOptionPane.showMessageDialog(null, "Login inválido", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                dispose();
                CandidateStore store = CandidateStore.getInstance();
                store.setToken(receivedMessage.data().token());
                CandidateArea candidateArea = new CandidateArea();
                candidateArea.setVisible(true);
            } catch (IOException err) {
                throw new RuntimeException(err);
            }
        });
    }


    public static void main(String[] args) {
        LoginUser dialog = new LoginUser();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
