package client.views;

import client.store.RecruiterStore;
import client.views.recruiter.RecruiterJobs;
import enums.Available;
import enums.Operations;
import enums.Statuses;
import helpers.singletons.IOConnection;
import models.Job;
import records.*;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class SearchableAndAvailable extends JDialog {
    private JPanel contentPane;
    private JButton buttonCancel;
    private JComboBox jobsRegistered;
    private JCheckBox availableCheckBox;
    private JCheckBox searchableCheckBox;
    List<JobToResponse> jobs = new ArrayList<>();
    RecruiterStore recruiterStore = RecruiterStore.getInstance();
    IOConnection io = IOConnection.getInstance();

    public SearchableAndAvailable() {
        setContentPane(contentPane);
        setModal(true);

        jobs = getJobSet();

        for (JobToResponse job: jobs) {
            jobsRegistered.addItem(job.skill());
        }

        availableCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onAvailable();
            }
        });

        searchableCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSearchable();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onAvailable() {
        JobToResponse job = jobs.get(jobsRegistered.getSelectedIndex());
        Request<SetJobAvailableRequest> req = new Request<>(Operations.SET_JOB_AVAILABLE, recruiterStore.getToken(), new SetJobAvailableRequest(job.id(), availableCheckBox.isSelected() ? Available.YES : Available.NO));

        io.send(req);

        try {
            Response<?> response = io.receive(Response.class);
            if (response.status() == Statuses.ERROR) {
                JOptionPane.showMessageDialog(null, "Error updating job availability");
            }
            else {
                JOptionPane.showMessageDialog(null, "Job availability updated successfully");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void onSearchable(){
        JobToResponse job = jobs.get(jobsRegistered.getSelectedIndex());
        Request<SetJobSearchableRequest> req = new Request<>(Operations.SET_JOB_SEARCHABLE, recruiterStore.getToken(), new SetJobSearchableRequest(job.id(), searchableCheckBox.isSelected() ? Available.YES : Available.NO));

        io.send(req);

        try {
            Response<?> response = io.receive(Response.class);
            if (response.status() == Statuses.ERROR) {
                JOptionPane.showMessageDialog(null, "Error updating job searchable");
            }
            else {
                JOptionPane.showMessageDialog(null, "Job availability updated searchable");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void onCancel() {
        dispose();

        RecruiterJobs recruiterJobs = new RecruiterJobs();
        recruiterJobs.pack();
recruiterJobs.setVisible(true);

    }

    private List<JobToResponse> getJobSet() {
        Request<?> request = new Request<>(Operations.LOOKUP_JOBSET, recruiterStore.getToken());
        io.send(request);

        try {
            Response<JobSetResponse> response = io.receive(JobSetResponse.class);

            return response.withDataClass(JobSetResponse.class).data().jobset();
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<>();
        }
    }

    public static void main(String[] args) {
        SearchableAndAvailable dialog = new SearchableAndAvailable();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
