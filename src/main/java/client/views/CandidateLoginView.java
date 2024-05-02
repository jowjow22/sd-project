package client.views;

import com.google.gson.internal.LinkedTreeMap;
import enums.Operations;
import enums.Statuses;
import helpers.ClientConnection;
import helpers.Json;
import records.CandidateLoginRequest;
import records.Request;
import records.Response;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class CandidateLoginView extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField candidateLoginEmailField;
    private JPasswordField candidateLoginPasswordField;
    private JButton buttonSignUpCandidate;

    public CandidateLoginView() {
        setContentPane(contentPane);
        setMinimumSize(new Dimension(500, 500));
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        buttonSignUpCandidate.addActionListener(e -> goToSignUpCandidate());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e ->
                onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        ClientConnection clientConnection = ClientConnection.getInstance();

        CandidateLoginRequest loginModel = new CandidateLoginRequest(candidateLoginEmailField.getText(), new String(candidateLoginPasswordField.getPassword()));
        Request<CandidateLoginRequest> request = new Request<>(Operations.LOGIN_CANDIDATE, loginModel);

        clientConnection.send(request);

        try {
            Response<?> response = clientConnection.receive();
            LinkedTreeMap<String, ?> data = (LinkedTreeMap<String, ?>) response.data();

            if (response.status().equals(Statuses.INVALID_LOGIN)){
                JOptionPane.showMessageDialog(null, "Incorrect fields");
                return;
            }

            dispose();
            CandidateHome candidateHome = new CandidateHome((String) data.get("token"));
            candidateHome.setVisible(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void goToSignUpCandidate() {
        dispose();
        CandidateSignUp candidateSignUp = new CandidateSignUp();
        candidateSignUp.setVisible(true);
    }

    public static void main(String[] args) {
        CandidateLoginView dialog = new CandidateLoginView();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
