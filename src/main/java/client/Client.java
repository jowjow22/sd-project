package client;
import java.io.*;
import java.net.*;

import client.views.LoginOptions;
import helpers.ClientConnection;
import client.views.StartConnection;

public class Client {
    public static void main(String[] args) throws IOException {
        StartConnection startConnection = new StartConnection();
        String serverHost = startConnection.getServerIp();
        int serverPort = startConnection.getServerPort();

        ClientConnection clientConnection = ClientConnection.getInstance(serverHost, serverPort);

        try {
            clientConnection.connect();
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + serverHost);
            System.exit(1);
        } catch (IOException e) {
            System.exit(1);
        }

        LoginOptions loginOptions = new LoginOptions();
        loginOptions.setVisible(true);
    }
}
