package client.views.candidate;

import client.store.CandidateStore;
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

public class CandidateSkillset extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox skill;
    private JSpinner yearsOfExperience;
    private JTable skillsetTable;
    private JButton updateButton;
    private JButton deleteButton;
    private SkillSetResponse skillSetResponse;
    CandidateStore candidateStore = CandidateStore.getInstance();
    IOConnection io = IOConnection.getInstance();
    DefaultTableModel model = new DefaultTableModel();

    public CandidateSkillset() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setMinimumSize(new java.awt.Dimension(400, 400));

        model.setColumnIdentifiers(new Object[]{"Skill", "Experience", "Update Button", "Delete Button"});


        List<ExperienceToResponse> skillset = getSkillSet();

        for(ExperienceToResponse experience: skillset){
            model.addRow(new Object[]{experience.skill(), experience.experience()});

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
                DeleteExperience deleteExperience = new DeleteExperience();
                deleteExperience.pack();
                deleteExperience.setVisible(true);
            }
        });

        updateButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent actionEvent) {
                dispose();
                UpdateExperience updateExperience = new UpdateExperience();
                updateExperience.pack();
                updateExperience.setVisible(true);
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
            Request<IncludeSkillRequest> request = new Request<>(Operations.INCLUDE_SKILL, candidateStore.getToken(), new IncludeSkillRequest(skillToInclude, yearsOfExperience.getValue().toString()));

            io.send(request);

            Response<?> response = io.receive(Object.class);
            if (response.status() == Statuses.SUCCESS) {
                JOptionPane.showMessageDialog(this, "Skill included successfully");

                try{
                    List<ExperienceToResponse> skillSet = getSkillSet();
                    model.setRowCount(0);
                    for(ExperienceToResponse experience: skillSet){
                        model.addRow(new Object[]{experience.skill(), experience.experience()});
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

    private List<ExperienceToResponse> getSkillSet() {
        Request<?> request = new Request<>(Operations.LOOKUP_SKILLSET, candidateStore.getToken());
        io.send(request);

        try {
            Response<SkillSetResponse> response = io.receive(SkillSetResponse.class);
            skillSetResponse = response.withDataClass(SkillSetResponse.class).data();

            return skillSetResponse.skillset();
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<>();
        }
    }

    private void onCancel() {
        dispose();
        CandidateArea candidateArea = new CandidateArea();
        candidateArea.pack();
        candidateArea.setVisible(true);
    }

    public static void main(String[] args) {
        CandidateSkillset dialog = new CandidateSkillset();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
