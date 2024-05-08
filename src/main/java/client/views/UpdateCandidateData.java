package client.views;

import client.store.CandidateStore;
import enums.Operations;
import enums.Statuses;
import helpers.singletons.IOConnection;
import models.Candidate;
import records.CandidateSignUpRequest;
import records.Request;
import records.Response;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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
        CandidateSignUpRequest candidateSignUpRequest = new CandidateSignUpRequest(name.getText(), email.getText(), new String(password.getPassword()));
        CandidateStore candidateStore = CandidateStore.getInstance();
        Request<CandidateSignUpRequest> request = new Request<>(Operations.UPDATE_ACCOUNT_CANDIDATE, candidateStore.getToken(), candidateSignUpRequest);
        IOConnection io = IOConnection.getInstance();
        io.send(request);

        try{
            Response<?> response = io.receive(Response.class);
            if(response.status().equals(Statuses.SUCCESS)){
                dispose();
                JOptionPane.showMessageDialog(null, "Account updated successfully");
                CandidateArea candidateArea = new CandidateArea();
                candidateArea.setVisible(true);
            }
            else{
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
