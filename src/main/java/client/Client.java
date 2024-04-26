package client;
import java.io.*;
import java.net.*;

import client.views.LoginUser;
import client.views.StartConnection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import records.CandidateLoginResponse;
import records.Request;
import records.Response;

public class Client {
    public static void main(String[] args) throws IOException {
        StartConnection startConnection = new StartConnection();
        LoginUser loginUser = new LoginUser();


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

        Gson gson = new GsonBuilder()
                .create();
        loginUser.setVisible(true);


        PrintWriter finalOut = out;
        BufferedReader finalIn = in;
        loginUser.Callback(request -> {
            String json = gson.toJson(request);
            System.out.println(json);
            finalOut.println(json);
            Response receivedMessage = null;
            try {
                receivedMessage = gson.fromJson(finalIn.readLine(), Response.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            LinkedTreeMap data = (LinkedTreeMap) receivedMessage.getData();
            String toJson = gson.toJson(data);
            CandidateLoginResponse candidateLogin = gson.fromJson(toJson, CandidateLoginResponse.class);
            System.out.println("Token: " + candidateLogin.token());
            return null;
        });

    }
}
