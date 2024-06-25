package client.views.recruiter;

import client.store.RecruiterStore;
import client.views.SearchableAndAvailable;
import enums.Available;
import enums.Operations;
import enums.Statuses;
import helpers.singletons.IOConnection;
import records.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecruiterJobs extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox skill;
    private JSpinner yearsOfExperience;
    private JTable skillsetTable;
    private JButton updateButton;
    private JButton deleteButton;
    private JCheckBox availableCheckbox;
    private JCheckBox searchableCheckbox;
    private JButton changeAvailableOrSearchableButton;
    private JobSetResponse jobsetResponse;
    RecruiterStore recruiterStore = RecruiterStore.getInstance();
    IOConnection io = IOConnection.getInstance();
    DefaultTableModel model = new DefaultTableModel();

    public RecruiterJobs() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setMinimumSize(new java.awt.Dimension(400, 400));

        model.setColumnIdentifiers(new Object[]{"Skill", "Experience"});


        List<JobToResponse> jobset = getJobset();

        for(JobToResponse job: jobset){
            model.addRow(new Object[]{job.skill(), job.experience()});

        }

        skillsetTable.setModel(model);


        skill.addItem("NodeJs");
        skill.addItem("JavaScript");
        skill.addItem("Java");
        skill.addItem("C");
        skill.addItem("HTML");
        skill.addItem("CSS");
        skill.addItem("React");
        skill.addItem("ReactNative");
        skill.addItem("TypeScript");
        skill.addItem("Ruby");


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

        deleteButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent actionEvent) {
                dispose();
                DeleteJob deleteJob = new DeleteJob();
                deleteJob.pack();
                deleteJob.setVisible(true);
            }
        });

        changeAvailableOrSearchableButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent actionEvent) {
                dispose();
                SearchableAndAvailable searchableAndAvailable = new SearchableAndAvailable();
                searchableAndAvailable.pack();
                searchableAndAvailable.setVisible(true);
            }
        });

        updateButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent actionEvent) {
                dispose();
                UpdateJob updateJob = new UpdateJob();
                updateJob.pack();
                updateJob.setVisible(true);
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
        try {
            String skillToInclude = (String) skill.getSelectedItem();
            Available isAvailable = availableCheckbox.isSelected() ? Available.YES : Available.NO;
            Available isSearchable = searchableCheckbox.isSelected() ? Available.YES : Available.NO;
            Request<IncludeJobRequest> request = new Request<>(Operations.INCLUDE_JOB, recruiterStore.getToken(), new IncludeJobRequest(skillToInclude, yearsOfExperience.getValue().toString(), isAvailable, isSearchable));

            io.send(request);

            Response<?> response = io.receive(Object.class);
            if (response.status() == Statuses.SUCCESS) {
                JOptionPane.showMessageDialog(this, "Job included successfully");

                try{
                    List<JobToResponse> jobset = getJobset();
                    model.setRowCount(0);
                    for(JobToResponse job: jobset){
                        model.addRow(new Object[]{job.skill(), job.experience()});
                    }

                    skillsetTable.setModel(model);

                }
                catch (Exception e){
                    System.out.println(e);
                }

            } else {
                JOptionPane.showMessageDialog(this, "Failed to include skill");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<JobToResponse> getJobset() {
        Request<?> request = new Request<>(Operations.LOOKUP_JOBSET, recruiterStore.getToken());
        io.send(request);

        try {
            Response<JobSetResponse> response = io.receive(JobSetResponse.class);
            jobsetResponse = response.withDataClass(JobSetResponse.class).data();

            return jobsetResponse.jobset();
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<>();
        }
    }

    private void onCancel() {
        dispose();
        RecruiterArea recruiterArea = new RecruiterArea();
        recruiterArea.pack();
        recruiterArea.setVisible(true);
    }

    public static void main(String[] args) {
        RecruiterJobs dialog = new RecruiterJobs();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
