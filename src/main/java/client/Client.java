package client;
import java.io.*;
import java.net.*;

import client.views.LoginUser;
import client.views.StartConnection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.Request;

public class Client {
    public static void main(String[] args) throws IOException {
        StartConnection startConnection = new StartConnection();
        LoginUser loginUser = new LoginUser();

        loginUser.setVisible(true);


        String serverHost = startConnection.getServerIp();
        int serverPort = startConnection.getServerPort();

        System.out.println("serverHost: " + serverHost);
        System.out.println("serverPort: " + serverPort);

        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            echoSocket = new Socket(serverHost, serverPort);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                    echoSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + serverHost);
            System.exit(1);
        } catch (IOException e) {
            System.exit(1);
        }

        BufferedReader stdIn = new BufferedReader(
                new InputStreamReader(System.in));
        String userInput;
        Gson gson = new GsonBuilder()
                .create();

        System.out.print ("input: ");
        while ((userInput = stdIn.readLine()) != null) {
            Request sentMessage = new Request(userInput);
            String json = gson.toJson(sentMessage);
            out.println(json);
            Request receivedMessage = gson.fromJson(in.readLine(), Request.class);
            System.out.println("Server: " + receivedMessage.getMessage());
            System.out.print ("input: ");
        }

        out.close();
        in.close();
        stdIn.close();
        echoSocket.close();
    }
}
