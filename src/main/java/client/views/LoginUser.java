package client.views;

import enums.Operations;
import helpers.singletons.IOConnection;
import helpers.singletons.Json;
import lombok.Getter;
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

    @Getter
    private Request<CandidateLoginRequest> request;

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

            CandidateLoginRequest candidateLogin = new CandidateLoginRequest(userEmail, userPassword);

            this.request = new Request<CandidateLoginRequest>(Operations.LOGIN_CANDIDATE,candidateLogin);

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
        });
    }


    public static void main(String[] args) {
        LoginUser dialog = new LoginUser();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
