package client.views;

import client.store.CandidateStore;
import enums.Operations;
import enums.Statuses;
import helpers.singletons.IOConnection;
import models.Candidate;
import records.CandidateSignUpAndUpdateRequest;
import records.Request;
import records.Response;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

public class UpdateCandidateData extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField email;
    private JPasswordField password;
    private JTextField name;

    public UpdateCandidateData() {
        setContentPane(contentPane);
        setModal(true);
        setMinimumSize(new Dimension(400, 400));
        getRootPane().setDefaultButton(buttonOK);

        CandidateStore candidateStore = CandidateStore.getInstance();
        Candidate candidate = candidateStore.getCandidate();
        name.setText(candidate.getName());
        email.setText(candidate.getEmail());
        password.setText(candidate.getPassword());

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendUpdate();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });


        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });


        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void sendUpdate() {
        CandidateStore candidateStore = CandidateStore.getInstance();
        Candidate candidate = candidateStore.getCandidate();
        //verify if the fields are empty
        String nameToRequest = name.getText();
        String emailToRequest = email.getText();
        String passwordToRequest = new String(password.getPassword());

        if (!nameToRequest.isEmpty()){
            candidate.setName(nameToRequest);
        }
        if (!emailToRequest.isEmpty()){
            candidate.setEmail(emailToRequest);
        }
        if (!passwordToRequest.isEmpty()){
            candidate.setPassword(passwordToRequest);
        }

        System.out.println(nameToRequest + " " + emailToRequest + " " + passwordToRequest);
        CandidateSignUpAndUpdateRequest candidateSignUpRequest = new CandidateSignUpAndUpdateRequest(candidate);
        Request<CandidateSignUpAndUpdateRequest> request = new Request<>(Operations.UPDATE_ACCOUNT_CANDIDATE, candidateStore.getToken(), candidateSignUpRequest);
        IOConnection io = IOConnection.getInstance();
        io.send(request);

        try{
            Response<?> response = io.receive(Response.class);
            if(response.status().equals(Statuses.SUCCESS)){
                dispose();
                JOptionPane.showMessageDialog(null, "Account updated successfully");
                CandidateArea candidateArea = new CandidateArea();
                candidateArea.setVisible(true);
            } else if (response.status().equals(Statuses.INVALID_EMAIL)) {
                JOptionPane.showMessageDialog(null, "Email already in use");
            } else{
                JOptionPane.showMessageDialog(null, "Account update failed");
            }
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private void onCancel() {
        dispose();
        CandidateArea candidateArea = new CandidateArea();
        candidateArea.setVisible(true);
    }

    public static void main(String[] args) {
        UpdateCandidateData dialog = new UpdateCandidateData();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
