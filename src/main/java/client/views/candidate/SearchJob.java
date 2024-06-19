package client.views.candidate;

import client.store.CandidateStore;
import enums.Operations;
import helpers.singletons.IOConnection;
import records.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class SearchJob extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox skillsCombox;
    private JComboBox filterType;
    private JSpinner experience;
    private JButton addSkillFilter;

    List<String> skills = new ArrayList<>();

    String experienceValue = null;

    String filterTypeValue = null;
    CandidateStore candidateStore = CandidateStore.getInstance();

    IOConnection io = IOConnection.getInstance();
    public SearchJob() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setMinimumSize(new java.awt.Dimension(600, 400));


        skillsCombox.addItem("NodeJs");
        skillsCombox.addItem("JavaScript");
        skillsCombox.addItem("Java");
        skillsCombox.addItem("C");
        skillsCombox.addItem("HTML");
        skillsCombox.addItem("CSS");
        skillsCombox.addItem("React");
        skillsCombox.addItem("ReactNative");
        skillsCombox.addItem("TypeScript");
        skillsCombox.addItem("Ruby");

        filterType.addItem("AND");
        filterType.addItem("OR");

        filterType.setEnabled(false);
        buttonOK.setEnabled(false);
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        addSkillFilter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                skills.add((String) skillsCombox.getSelectedItem());

                buttonOK.setEnabled(true);
                if (experienceValue != null || (Integer)experience.getValue() > 0) {
                    experienceValue = experience.getValue().toString();
                    filterType.setEnabled(true);
                }
            }
        });

        experience.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                buttonOK.setEnabled(true);
                experienceValue =  experience.getValue().toString();
                if (skills.size() > 0 && (Integer) experience.getValue() > 0) {
                    filterType.setEnabled(true);
                }
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
        if (filterType.isEnabled()) {
            filterTypeValue = (String) filterType.getSelectedItem();
        }
        Request<SearchJobRequest> request = null;
        if (skills.size() == 0) {
           request = new Request<>(Operations.SEARCH_JOB, candidateStore.getToken(), new SearchJobRequest(null, experienceValue, filterTypeValue));
        }
        else{
            request = new Request<>(Operations.SEARCH_JOB, candidateStore.getToken(), new SearchJobRequest(skills, experienceValue, filterTypeValue));
        }
        io.send(request);

        try{
            Response<JobSetResponse> response = io.receive(JobSetResponse.class);
            String formatted = "";
            for(JobToResponse job : response.data().jobset()){
                formatted += job.skill() + " - " + job.experience() + "\n";
            }
            if (formatted.equals("")) {
                throw new Exception("No jobs found");
            }
            JOptionPane.showMessageDialog(this, formatted);
        }catch (Exception e){
            System.out.println(e);
            JOptionPane.showMessageDialog(this, "No jobs found");
        }
    }

    private void onCancel() {
        dispose();
        CandidateArea candidateArea = new CandidateArea();
        candidateArea.pack();
        candidateArea.setVisible(true);
    }

    public static void main(String[] args) {
        SearchJob dialog = new SearchJob();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
