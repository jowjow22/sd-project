package client.views;

import enums.Operations;
import lombok.Getter;
import records.CandidateLoginRequest;
import records.Request;

import javax.swing.*;
import java.awt.*;
import java.util.function.Function;

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
        });

        buttonOK.addActionListener(e -> {
            userEmail = email.getText();
            userPassword = new String(password.getPassword());

            CandidateLoginRequest candidateLogin = new CandidateLoginRequest(userEmail, userPassword);

            this.request = new Request<CandidateLoginRequest>(Operations.LOGIN_CANDIDATE,candidateLogin);

            dispose();
        });
    }

    public void Callback(Function callback){
        callback.apply(this.request);
    }

    public static void main(String[] args) {
        LoginUser dialog = new LoginUser();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
