package helpers.singletons;

import lombok.Setter;
import records.IpAdressess;
import server.IpLogs;

import javax.swing.*;
import java.util.HashSet;

public class IpAddressesStore {
    private final HashSet<IpAdressess> ipAdressess;

    private JPanel buttonsPanel;
    @Setter
    private JList logsList;
    private static IpAddressesStore instance = null;
    public IpAddressesStore() {
        ipAdressess = new HashSet<>();
    }

    public static IpAddressesStore getInstance(){
        if(instance == null){
            instance = new IpAddressesStore();
        }
        return instance;
    }
    public void updateLogs(String ip, String log){
        IpAdressess ipAdress = ipAdressess.stream().filter(ipAdressess -> ipAdressess.ip().equals(ip)).findAny().orElse(null);
        ipAdressess.remove(ipAdress);
        ipAdress.logs().add(log);
        ipAdressess.add(ipAdress);
    }
    public void addIp(String ip, String port){
        ipAdressess.add(new IpAdressess(ip, port));
    }
    public void removeIp(String ip){
        IpAdressess ipAdress = ipAdressess.stream().filter(ipAdressess -> ipAdressess.ip().equals(ip)).findAny().orElse(null);
        ipAdressess.remove(ipAdress);
    }

    public HashSet<IpAdressess> getIpAdressess() {
        return ipAdressess;
    }
    public void setPanelModel(JPanel panel){
        buttonsPanel = panel;
    }

    public void updatePanel(){
        buttonsPanel.removeAll();
        for (IpAdressess ip : ipAdressess) {
            JButton button = new JButton(ip.ip() + ":" + ip.port());
            button.addActionListener(e -> {
                for (String log : ip.logs()) {
                    System.out.println(log);
                }
                new IpLogs(ip.logs());
            });
            button.setSize(100, 50);
            buttonsPanel.add(button);
        }
        buttonsPanel.revalidate();
        buttonsPanel.repaint();
    }
}
