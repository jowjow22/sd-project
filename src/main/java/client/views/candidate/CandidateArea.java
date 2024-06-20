package client.views.candidate;

import client.store.CandidateStore;
import client.views.SignOptions;
import enums.Operations;
import enums.Statuses;
import helpers.singletons.IOConnection;
import models.Candidate;
import records.Request;
import records.Response;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;

public class CandidateArea extends JDialog {
    private JPanel contentPane;
    private JTable candidateDataTable;
    private JScrollPane scroll;
    private JButton updateAccount;
    private JButton deleteAccountButton;
    private JButton logoutButton;
    private JButton mySkills;
    private JButton searchJobs;

    public CandidateArea() {
        setContentPane(contentPane);
        setModal(true);
        setMinimumSize(new Dimension(600, 400));
        IOConnection io = IOConnection.getInstance();

        CandidateStore candidateStore = CandidateStore.getInstance();

        DefaultTableModel model = new DefaultTableModel();


        try {
            Request request = new Request<>(Operations.LOOKUP_ACCOUNT_CANDIDATE, candidateStore.getToken());


            io.send(request);
            Response<Candidate> response = io.receive(Candidate.class);
            Candidate candidate = response.data();

            CandidateStore store = CandidateStore.getInstance();
            store.setCandidate(candidate);



        } catch (IOException err) {
            throw new RuntimeException(err);
        }

        Candidate candidate = candidateStore.getCandidate();
        List<Candidate> candidates = List.of(candidate);

        model.setColumnIdentifiers(new Object[]{"Name", "Email"});

        for (Candidate c : candidates) {
            model.addRow(new Object[]{c.getName(), c.getEmail()});
        }

        candidateDataTable.setModel(model);



        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Request<?> request = new Request<>(Operations.LOGOUT_CANDIDATE, candidateStore.getToken());
                io.send(request);
                try {
                    Response<Object> response = io.receive(Object.class);

                    if (response.status().equals(Statuses.SUCCESS)) {
                        dispose();
                        SignOptions signOptions = new SignOptions();
                        signOptions.pack();
                        signOptions.setVisible(true);
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "Error logging out");
                    }

                } catch (Exception err) {
                    throw new RuntimeException(err);
                }
            }
        });
        searchJobs.addActionListener(e -> {
            dispose();
            SearchJob searchJob = new SearchJob();
            searchJob.setVisible(true);
        });
        mySkills.addActionListener(e -> {
            dispose();
            CandidateSkillset candidateSkillset = new CandidateSkillset();
            candidateSkillset.setVisible(true);
        });
        updateAccount.addActionListener(e -> {
            dispose();
            UpdateCandidateData updateCandidateData = new UpdateCandidateData();
            updateCandidateData.setVisible(true);
        });
        deleteAccountButton.addActionListener(e -> {
            Request request = new Request<>(Operations.DELETE_ACCOUNT_CANDIDATE, candidateStore.getToken());
            io.send(request);
            try {
                Response<Object> response = io.receive(Object.class);

                if (response.status().equals(Statuses.SUCCESS)) {
                    dispose();
                    JOptionPane.showMessageDialog(null, "Account deleted successfully");
                    SignOptions signOptions = new SignOptions();
                    signOptions.pack();
                    signOptions.setVisible(true);
                }
                else {
                    JOptionPane.showMessageDialog(null, "Error deleting account");
                }

            } catch (Exception err) {
                throw new RuntimeException(err);
            }
        });
    }


    public static void main(String[] args) {
        CandidateArea dialog = new CandidateArea();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
