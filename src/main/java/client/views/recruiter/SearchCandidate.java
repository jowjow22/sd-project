package client.views.recruiter;

import client.store.CandidateStore;
import client.store.RecruiterStore;
import client.views.candidate.CandidateArea;
import enums.Operations;
import enums.Statuses;
import helpers.singletons.Database;
import helpers.singletons.IOConnection;
import models.Candidate;
import records.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class SearchCandidate extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox skillsCombox;
    private JComboBox filterType;
    private JSpinner experience;
    private JButton addSkillFilter;
    private JPanel candidatesPanel;

    List<String> skills = new ArrayList<>();

    String experienceValue = null;

    String filterTypeValue = null;
    RecruiterStore recruiterStore = RecruiterStore.getInstance();

    IOConnection io = IOConnection.getInstance();
    public SearchCandidate() {
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
        Request<SearchCandidateRequest> request = null;
        if (skills.size() == 0) {
           request = new Request<>(Operations.SEARCH_CANDIDATE, recruiterStore.getToken(), new SearchCandidateRequest(null, experienceValue, filterTypeValue));
        }
        else{
            request = new Request<>(Operations.SEARCH_CANDIDATE, recruiterStore.getToken(), new SearchCandidateRequest(skills, experienceValue, filterTypeValue));
        }
        io.send(request);

        try{
            Response<SearchCandidatesResponse> response = io.receive(SearchCandidatesResponse.class);
            candidatesPanel.removeAll();
            candidatesPanel.revalidate();
            candidatesPanel.repaint();
            candidatesPanel.setLayout(new GridLayout(response.data().profile().size(), 1));
            HashMap<String, String> candidates = new HashMap<>();
            for (CandidateToSearchResponse candidate : response.data().profile()) {
                candidates.put(candidate.id(), candidate.name());
            }
            List<String> UniqueCandidates = new ArrayList<>();
            for (Map.Entry<String, String> entry : candidates.entrySet()) {
                UniqueCandidates.add(entry.getKey() + " - " + entry.getValue());
            }
            for(String candidate : UniqueCandidates){
                JButton button = new JButton(candidate);
                button.addActionListener(e -> {
                    String[] candidateData = candidate.split(" - ");
                    Request<ChooseCandidateRequest> chooseCandidateRequest = new Request<>(Operations.CHOOSE_CANDIDATE, recruiterStore.getToken(), new ChooseCandidateRequest(candidateData[0]));
                    io.send(chooseCandidateRequest);
                    try{
                        Response<Object> chooseCandidateResponse = io.receive(Object.class);
                        if (chooseCandidateResponse.status().equals(Statuses.SUCCESS)) {
                            JOptionPane.showMessageDialog(this, "Candidate choosed");
                        }
                        else {
                            JOptionPane.showMessageDialog(this, "Error choosing candidate");
                        }
                    }catch (Exception err){
                        throw new RuntimeException(err);
                    }
                });
                candidatesPanel.add(button);
            }
            candidatesPanel.revalidate();
            candidatesPanel.repaint();
            if (response.data().profile_size().equals("0")) {
                throw new Exception("No candidates found");
            }
        }catch (Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "No candidates found");
        }
    }

    private void onCancel() {
        dispose();
        RecruiterArea recruiterArea = new RecruiterArea();
        recruiterArea.pack();
        recruiterArea.setVisible(true);
    }

    public static void main(String[] args) {
        SearchCandidate dialog = new SearchCandidate();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
