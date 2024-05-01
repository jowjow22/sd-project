package client.views;

import enums.Operations;
import enums.Statuses;
import helpers.ClientConnection;
import records.CandidateLogoutRequest;
import records.Request;
import records.Response;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class CandidateHome extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonLogout;
    private JLabel userName;

    public CandidateHome() {
        setContentPane(contentPane);
        setMinimumSize(new Dimension(500, 500));
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonLogout.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        dispose();
        CandidateProfile candidateProfile = new CandidateProfile();
        candidateProfile.setVisible(true);
    }
    private void onCancel() {
        ClientConnection clientConnection = ClientConnection.getInstance();

        CandidateLogoutRequest logoutModel = new CandidateLogoutRequest("algumtoken");
        Request<CandidateLogoutRequest> request = new Request<>(Operations.LOGOUT_CANDIDATE, logoutModel);

        clientConnection.send(request);

        try {
            Response<?> response = clientConnection.receive();

            if (!(response.status().equals(Statuses.SUCCESS))){
                JOptionPane.showMessageDialog(null, "Something went wrong!");
                return;
            }

            dispose();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        dispose();
        CandidateLoginView candidateLoginView = new CandidateLoginView();
        candidateLoginView.setVisible(true);
    }

    public static void main(String[] args) {
        CandidateHome dialog = new CandidateHome();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
