package server;

import lombok.Getter;
import records.IpAdressess;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class IpAdresses {
    @Getter
    private JPanel buttonsPanel;

    public IpAdresses(HashSet<IpAdressess> ips) {
        List<JButton> ipButtons = new ArrayList<>();
        for (IpAdressess ip : ips) {
            JButton button = new JButton(ip.ip() + ":" + ip.port());
            ipButtons.add(button);
        }

        for (JButton button : ipButtons) {
            buttonsPanel.add(button);
        }

        JFrame frame = new JFrame("IpAdresses");
        frame.setContentPane(buttonsPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setMinimumSize(new Dimension(400, 400));
    }

    public void updateTable(HashSet<IpAdressess> ips) {
        buttonsPanel.setLayout(new GridLayout(ips.size()+2, 1));
        buttonsPanel.removeAll();
        List<JButton> ipButtons = new ArrayList<>();
        for (IpAdressess ip : ips) {
            JButton button = new JButton(ip.ip() + ":" + ip.port());
            button.addActionListener(e -> {
                for (String log : ip.logs()) {
                    System.out.println(log);
                }
                new IpLogs(ip.logs());
            });
            button.setSize(new Dimension(100, 50));
            ipButtons.add(button);
        }

        for (JButton button : ipButtons) {
            buttonsPanel.add(button);
        }
        buttonsPanel.revalidate();
        buttonsPanel.repaint();
    }
}
