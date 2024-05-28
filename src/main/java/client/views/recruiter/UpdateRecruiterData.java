package client.views.recruiter;

import client.store.CandidateStore;
import client.store.RecruiterStore;
import enums.Operations;
import enums.Statuses;
import helpers.singletons.IOConnection;
import models.Candidate;
import models.Recruiter;
import records.CandidateSignUpAndUpdateRequest;
import records.RecruiterSignUpAndUpdateRequest;
import records.Request;
import records.Response;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class UpdateRecruiterData extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField email;
    private JPasswordField password;
    private JTextField name;

    private  JTextField industry;

    private  JTextField description;

    public UpdateRecruiterData() {
        setContentPane(contentPane);
        setModal(true);
        setMinimumSize(new Dimension(400, 400));
        getRootPane().setDefaultButton(buttonOK);

        RecruiterStore recruiterStore = RecruiterStore.getInstance();
        Recruiter recruiter = recruiterStore.getRecruiter();
        name.setText(recruiter.getName());
        email.setText(recruiter.getEmail());
        password.setText(recruiter.getPassword());
        industry.setText(recruiter.getIndustry());
        description.setText(recruiter.getDescription());

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
        RecruiterStore recruiterStore = RecruiterStore.getInstance();
        Recruiter recruiter = recruiterStore.getRecruiter();
        //verify if the fields are empty
        String nameToRequest = name.getText();
        String emailToRequest = email.getText();
        String passwordToRequest = new String(password.getPassword());
        String industryToRequest = industry.getText();
        String descriptionToRequest = description.getText();

        if (!nameToRequest.isEmpty()){
            recruiter.setName(nameToRequest);
        }
        if (!emailToRequest.isEmpty()){
            recruiter.setEmail(emailToRequest);
        }
        if (!passwordToRequest.isEmpty()){
            recruiter.setPassword(passwordToRequest);
        }
        if (!industryToRequest.isEmpty()){
            recruiter.setIndustry(industryToRequest);
        }
        if (!descriptionToRequest.isEmpty()){
            recruiter.setDescription(descriptionToRequest);
        }

        RecruiterSignUpAndUpdateRequest recruiterSignUpRequest = new RecruiterSignUpAndUpdateRequest(recruiter);
        Request<RecruiterSignUpAndUpdateRequest> request = new Request<>(Operations.UPDATE_ACCOUNT_RECRUITER, recruiterStore.getToken(), recruiterSignUpRequest);
        IOConnection io = IOConnection.getInstance();
        io.send(request);

        try{
            Response<?> response = io.receive(Response.class);
            if(response.status().equals(Statuses.SUCCESS)){
                dispose();
                JOptionPane.showMessageDialog(null, "Account updated successfully");
                RecruiterArea recruiterArea = new RecruiterArea();
                recruiterArea.setVisible(true);
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
        RecruiterArea recruiterArea = new RecruiterArea();
        recruiterArea.setVisible(true);
    }

    public static void main(String[] args) {
        UpdateRecruiterData dialog = new UpdateRecruiterData();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
