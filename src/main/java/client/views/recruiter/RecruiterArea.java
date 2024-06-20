package client.views.recruiter;

import client.store.RecruiterStore;
import client.views.SignOptions;
import enums.Operations;
import enums.Statuses;
import helpers.singletons.IOConnection;
import models.Recruiter;
import records.RecruiterResponse;
import records.Request;
import records.Response;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;

public class RecruiterArea extends JDialog {
    private JPanel contentPane;
    private JTable recruiterDataTable;
    private JScrollPane scroll;
    private JButton updateAccount;
    private JButton deleteAccountButton;
    private JButton logoutButton;
    private JButton jobsView;

    public RecruiterArea() {
        setContentPane(contentPane);
        setModal(true);
        setMinimumSize(new Dimension(400, 400));
        IOConnection io = IOConnection.getInstance();

        RecruiterStore recruiterStore = RecruiterStore.getInstance();

        DefaultTableModel model = new DefaultTableModel();


        try {
            Request request = new Request<>(Operations.LOOKUP_ACCOUNT_RECRUITER, recruiterStore.getToken());


            io.send(request);
            Response<RecruiterResponse> response = io.receive(RecruiterResponse.class);
            Recruiter recruiter = new Recruiter();
            recruiter.setName(response.data().name());
            recruiter.setEmail(response.data().email());
            recruiter.setIndustry(response.data().industry());
            recruiter.setDescription(response.data().description());

            RecruiterStore store = RecruiterStore.getInstance();
            store.setRecruiter(recruiter);



        } catch (IOException err) {
            throw new RuntimeException(err);
        }

        Recruiter recruiter = recruiterStore.getRecruiter();
        List<Recruiter> recruiters = List.of(recruiter);

        model.setColumnIdentifiers(new Object[]{"Name", "Email", "Industry", "Description"});

        for (Recruiter r : recruiters) {
            model.addRow(new Object[]{r.getName(), r.getEmail(), r.getIndustry(), r.getDescription()});
        }

        recruiterDataTable.setModel(model);



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
                Request<?> request = new Request<>(Operations.LOGOUT_RECRUITER, recruiterStore.getToken());
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
        updateAccount.addActionListener(e -> {
            dispose();
            UpdateRecruiterData updateRecruiterData = new UpdateRecruiterData();
            updateRecruiterData.setVisible(true);
        });
        jobsView.addActionListener(e -> {
            dispose();
            RecruiterJobs recruiterJobs = new RecruiterJobs();
            recruiterJobs.pack();
            recruiterJobs.setVisible(true);
        });
        deleteAccountButton.addActionListener(e -> {
            Request request = new Request<>(Operations.DELETE_ACCOUNT_RECRUITER, recruiterStore.getToken());
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
        RecruiterArea dialog = new RecruiterArea();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
