package client;
import java.io.*;
import java.net.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.FieldNamingPolicy;
import models.MessageModel;

public class Client {
    public static void main(String[] args) throws IOException {
        StartConnection startConnection = new StartConnection();

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
            MessageModel sendedMessage = new MessageModel(userInput);
            String json = gson.toJson(sendedMessage);
            out.println(json);
            MessageModel receivedMessage = gson.fromJson(in.readLine(), MessageModel.class);
            System.out.println("Server: " + receivedMessage.getMessage());
            System.out.print ("input: ");
        }

        out.close();
        in.close();
        stdIn.close();
        echoSocket.close();
    }
}
