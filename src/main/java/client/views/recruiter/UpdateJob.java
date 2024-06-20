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

public class UpdateJob extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox currentSkill;
    private JComboBox newSkill;
    private JSpinner spinner1;

    List<JobToResponse> jobs = new ArrayList<>();
    RecruiterStore recruiterStore = RecruiterStore.getInstance();
    IOConnection io = IOConnection.getInstance();

    public UpdateJob() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);


        newSkill.addItem("NodeJs");
        newSkill.addItem("JavaScript");
        newSkill.addItem("Java");
        newSkill.addItem("C");
        newSkill.addItem("HTML");
        newSkill.addItem("CSS");
        newSkill.addItem("React");
        newSkill.addItem("ReactNative");
        newSkill.addItem("TypeScript");
        newSkill.addItem("Ruby");

        jobs = getSkillSet();

        for (JobToResponse job: jobs) {
            currentSkill.addItem(job.skill());
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

        currentSkill.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentSkill.getSelectedIndex() > jobs.size()) {
                    return;
                }
                JobToResponse job = jobs.get(currentSkill.getSelectedIndex());
                spinner1.setValue(Integer.parseInt(job.experience()));
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
        String jobId = jobs.get(currentSkill.getSelectedIndex()).id();
        Request<UpdateJobRequest> request = new Request<>(Operations.UPDATE_JOB, recruiterStore.getToken(), new UpdateJobRequest(jobId, spinner1.getValue().toString(), (String) newSkill.getSelectedItem()));
        io.send(request);

        try {
            Response<?> response = io.receive(Object.class);
            if (response.status() == Statuses.SUCCESS) {
                JOptionPane.showMessageDialog(null, "Skill updated successfully");
            } else if (response.status() == Statuses.SKILL_EXISTS){
                JOptionPane.showMessageDialog(null, "Experience already registered");
            } else if (response.status() == Statuses.SKILL_NOT_FOUND) {
                JOptionPane.showMessageDialog(null, "Skill not found");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to update skill");
        }
    }

    private void onCancel() {
        dispose();
        RecruiterJobs recruiterJobs = new RecruiterJobs();
        recruiterJobs.pack();
        recruiterJobs.setVisible(true);
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
        UpdateJob dialog = new UpdateJob();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
