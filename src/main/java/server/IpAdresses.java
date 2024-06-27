package server;

import records.IpAdressess;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class IpAdresses {
    private JPanel panel1;
    private JTable adresses;

    public IpAdresses(List<IpAdressess> ips) {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{"Ip", "Port"});

        for (IpAdressess ip : ips) {
            model.addRow(new Object[]{ip.ip(), ip.port()});
        }

        adresses.setModel(model);

        JFrame frame = new JFrame("IpAdresses");
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void updateTable(List<IpAdressess> ips) {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{"Ip", "Port"});

        for (IpAdressess ip : ips) {
            model.addRow(new Object[]{ip.ip(), ip.port()});
        }

        adresses.setModel(model);
    }
}
