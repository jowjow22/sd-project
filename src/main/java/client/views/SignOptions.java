package client.views;

import helpers.singletons.IOConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SignOptions extends JDialog {
    private JPanel contentPane;
    private JButton candidateButton;
    private JButton RECRUITER;
    private JButton exit;

    public SignOptions() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(candidateButton);
        setMinimumSize(new Dimension(300, 300));

        candidateButton.addActionListener(e -> {
            dispose();
            SignUpCandidate signUpCandidate = new SignUpCandidate();
            signUpCandidate.setVisible(true);
        });

        RECRUITER.addActionListener(e -> onCancel());

        exit.addActionListener(e -> onCancel());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }


    private void onCancel() {
        IOConnection io = IOConnection.getInstance();
        try {
            io.close();
        }catch (Exception e){
            System.exit(1);
        }
        dispose();
    }

    public static void main(String[] args) {
        SignOptions dialog = new SignOptions();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
