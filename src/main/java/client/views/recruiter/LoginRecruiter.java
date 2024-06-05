package client.views.recruiter;

import client.store.RecruiterStore;
import enums.Operations;
import enums.Statuses;
import helpers.singletons.IOConnection;
import records.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class LoginRecruiter extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;

    private JTextField email;
    private JPasswordField password;
    private String userEmail;
    private String userPassword;

    public LoginRecruiter() {
        setContentPane(contentPane);
        setModal(true);
        setMinimumSize(new Dimension(400, 400));
        getRootPane().setDefaultButton(buttonOK);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        buttonCancel.addActionListener(e -> {
            dispose();
            SignUpRecruiter signUpRecruiter = new SignUpRecruiter();
            signUpRecruiter.setVisible(true);
        });

        buttonOK.addActionListener(e -> {
            userEmail = email.getText();
            userPassword = new String(password.getPassword());



            IOConnection io = IOConnection.getInstance();

            CandidateLoginRequest candidateLogin = new CandidateLoginRequest(userEmail, userPassword);
            Request<CandidateLoginRequest> request = new Request<>(Operations.LOGIN_RECRUITER ,candidateLogin);
            io.send(request);
            Response<RecruiterLoginResponse> receivedMessage;
            try {
                receivedMessage = io.receive(RecruiterLoginResponse.class);

                if (receivedMessage.status().equals(Statuses.INVALID_LOGIN) || receivedMessage.status().equals(Statuses.INVALID_FIELD)){
                    JOptionPane.showMessageDialog(null, "Login inválido", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                dispose();
                RecruiterStore store = RecruiterStore.getInstance();
                store.setToken(receivedMessage.data().getToken());
                RecruiterArea recruiterArea = new RecruiterArea();
                recruiterArea.setVisible(true);
            } catch (IOException err) {
                throw new RuntimeException(err);
            }
        });
    }


    public static void main(String[] args) {
        LoginRecruiter dialog = new LoginRecruiter();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
