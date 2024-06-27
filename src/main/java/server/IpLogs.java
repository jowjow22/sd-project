package server;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class IpLogs {
    private JList logsList;

    public IpLogs(List<String> logs) {
        JFrame frame = new JFrame("IpLogs");
        frame.setContentPane(logsList);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String log : logs) {
            listModel.addElement(log);
        }
        logsList.setModel(listModel);

        frame.pack();
        frame.setVisible(true);
        frame.setMinimumSize(new Dimension(400, 400));

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
