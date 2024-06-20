package client.views.recruiter;

import client.store.CandidateStore;
import client.store.RecruiterStore;
import client.views.candidate.CandidateSkillset;
import enums.Operations;
import enums.Statuses;
import helpers.singletons.IOConnection;
import records.*;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class DeleteJob extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox userSkills;

    List<JobToResponse> jobs = new ArrayList<>();
    RecruiterStore recruiterStore = RecruiterStore.getInstance();
    IOConnection io = IOConnection.getInstance();

    public DeleteJob() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        jobs = getSkillSet();

        for (JobToResponse job: jobs) {
            userSkills.addItem(job.skill());
        }

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
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

    private void onOK() {
        String id = null;
        for (JobToResponse job: jobs) {
            if (job.skill().equals(userSkills.getSelectedItem())) {
                id = job.id();
            }
        }
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Skill not found");
            return;
        }
        Request<DeleteJobRequest> request = new Request<>(Operations.DELETE_JOB, recruiterStore.getToken(), new DeleteJobRequest(id));
        io.send(request);

        try {
            Response<?> response = io.receive(Object.class);
            if (response.status() == Statuses.SUCCESS) {
                JOptionPane.showMessageDialog(this, "Skill deleted successfully");
                dispose();
                RecruiterJobs dialog = new RecruiterJobs();
                dialog.pack();
                dialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Skill could not be deleted");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void onCancel() {
        dispose();
        RecruiterJobs dialog = new RecruiterJobs();
        dialog.pack();
        dialog.setVisible(true);
    }

    private List<JobToResponse> getSkillSet() {
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
        DeleteJob dialog = new DeleteJob();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
