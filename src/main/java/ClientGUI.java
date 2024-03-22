import server.Server;
import javax.swing.*;
import java.awt.*;

public class ClientGUI {
    private JTextField textField1;
    private JButton button1;
    private JPanel Jpanel;

    private final Server server = new Server();

    public ClientGUI() {
        button1.addActionListener(e -> server.uppercaseMessage(textField1.getText()));
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("App");
        frame.setContentPane(new ClientGUI().Jpanel);
        frame.setMinimumSize(new Dimension(500, 500));
        frame.setVisible(true);

        JFrame frame2 = new JFrame("App2");
        frame2.setContentPane(new ClientGUI().Jpanel);
        frame2.setMinimumSize(new Dimension(500, 500));
        frame2.setVisible(true);
    }
}
