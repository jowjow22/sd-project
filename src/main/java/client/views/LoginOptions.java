package client.views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginOptions extends JDialog {
    private JPanel contentPane;
    private JButton candidateButton;
    private JButton recruiterButton;

    public LoginOptions() {
        setContentPane(contentPane);
        setMinimumSize(new Dimension(500, 500));
        setModal(true);

        candidateButton.addActionListener(e -> {
            dispose();
            CandidateLoginView candidateLoginView = new CandidateLoginView();
            candidateLoginView.setVisible(true);
        });

        recruiterButton.addActionListener(e -> onCancel());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
        LoginOptions dialog = new LoginOptions();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
