package client.views;

import client.store.CandidateStore;
import enums.Operations;
import enums.Statuses;
import helpers.singletons.IOConnection;
import models.Candidate;
import records.Request;
import records.Response;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class CandidateArea extends JDialog {
    private JPanel contentPane;
    private JTable candidateDataTable;
    private JScrollPane scroll;
    private JButton updateAccount;
    private JButton deleteAccountButton;
    private JButton logoutButton;

    public CandidateArea() {
        setContentPane(contentPane);
        setModal(true);
        setMinimumSize(new Dimension(400, 400));
        IOConnection io = IOConnection.getInstance();

        CandidateStore candidateStore = CandidateStore.getInstance();

        Candidate candidate = candidateStore.getCandidate();

        List<Candidate> candidates = List.of(candidate);

        DefaultTableModel model = new DefaultTableModel();


        model.setColumnIdentifiers(new Object[]{"Name", "Email"});

        for (Candidate c : candidates) {
            model.addRow(new Object[]{c.getName(), c.getEmail()});
        }

        candidateDataTable.setModel(model);



        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Request<?> request = new Request<>(Operations.LOGOUT_CANDIDATE, candidateStore.getToken());
                io.send(request);
                try {
                    Response<Object> response = io.receive(Object.class);

                    if (response.status().equals(Statuses.SUCCESS)) {
                        dispose();
                        SignOptions signOptions = new SignOptions();
                        signOptions.pack();
                        signOptions.setVisible(true);
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "Error logging out");
                    }

                } catch (Exception err) {
                    throw new RuntimeException(err);
                }
            }
        });
    }


    public static void main(String[] args) {
        CandidateArea dialog = new CandidateArea();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
