package client.views.candidate;

import client.store.CandidateStore;
import enums.Operations;
import enums.Statuses;
import helpers.singletons.IOConnection;
import org.hibernate.sql.Update;
import records.*;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class UpdateExperience extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox currentSkill;
    private JComboBox newSkill;
    private JSpinner spinner1;
    CandidateStore candidateStore = CandidateStore.getInstance();
    IOConnection io = IOConnection.getInstance();

    public UpdateExperience() {
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

        List<ExperienceToResponse> experiences = getSkillSet();

        for (ExperienceToResponse experience: experiences) {
            currentSkill.addItem(experience.skill());
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
                if (currentSkill.getSelectedIndex() > experiences.size()) {
                    return;
                }
                ExperienceToResponse experience = experiences.get(currentSkill.getSelectedIndex());
                spinner1.setValue(experience.experience());
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
        // addyour code here
        Request<UpdateSkillRequest> request = new Request<>(Operations.UPDATE_SKILL, candidateStore.getToken(), new UpdateSkillRequest(currentSkill.getSelectedItem().toString(), (Integer) spinner1.getValue(), newSkill.getSelectedItem().toString()));
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
        CandidateSkillset candidateSkillset = new CandidateSkillset();
        candidateSkillset.pack();
        candidateSkillset.setVisible(true);
    }

    private List<ExperienceToResponse> getSkillSet() {
        Request<?> request = new Request<>(Operations.LOOKUP_SKILLSET, candidateStore.getToken());
        io.send(request);

        try {
            Response<SkillSetResponse> response = io.receive(SkillSetResponse.class);

            response.withDataClass(SkillSetResponse.class).data().skillset();

            return response.withDataClass(SkillSetResponse.class).data().skillset();
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<>();
        }
    }

    public static void main(String[] args) {
        UpdateExperience dialog = new UpdateExperience();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
