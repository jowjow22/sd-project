package client.views.candidate;

import client.store.CandidateStore;
import enums.Operations;
import enums.Statuses;
import helpers.singletons.IOConnection;
import records.*;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class DeleteExperience extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox userSkills;
    CandidateStore candidateStore = CandidateStore.getInstance();
    IOConnection io = IOConnection.getInstance();

    public DeleteExperience() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        List<ExperienceToResponse> experiences = getSkillSet();

        for (ExperienceToResponse experience: experiences) {
            userSkills.addItem(experience.skill());
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
        Request<DeleteSkillRequest> request = new Request<>(Operations.DELETE_SKILL, candidateStore.getToken(), new DeleteSkillRequest((String) userSkills.getSelectedItem()));
        io.send(request);

        try {
            Response<?> response = io.receive(Object.class);
            if (response.status() == Statuses.SUCCESS) {
                JOptionPane.showMessageDialog(this, "Skill deleted successfully");
                dispose();
                CandidateSkillset candidateSkillset = new CandidateSkillset();
                candidateSkillset.pack();
                candidateSkillset.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Skill could not be deleted");
            }
        } catch (Exception e) {
            System.out.println(e);
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
        DeleteExperience dialog = new DeleteExperience();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
