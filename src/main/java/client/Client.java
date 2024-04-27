package client;
import java.io.*;
import java.net.*;

import client.views.LoginUser;
import client.views.SignOptions;
import client.views.StartConnection;
import helpers.singletons.IOConnection;

public class Client {
    public static void main(String[] args) {
        StartConnection startConnection = new StartConnection();


        String serverHost = startConnection.getServerIp();
        int serverPort = startConnection.getServerPort();
        IOConnection io = IOConnection.getInstance(serverHost, serverPort);

        try {
            io.connect();
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + serverHost);
            System.exit(1);
        } catch (IOException e) {
            System.exit(1);
        }

        SignOptions signOptions = new SignOptions();
        signOptions.setVisible(true);


    }
}
