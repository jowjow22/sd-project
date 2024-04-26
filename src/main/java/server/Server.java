package server;
import java.io.*;
import java.net.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.Request;

public class Server extends Thread{
    private final Socket client;
    private final Gson gson = new GsonBuilder()
            .create();
    public static void main(String[] args)   {
        try {
            Server.startConnection();
        } catch (IOException e) {
            System.exit(1);
        }
    }
    private Server(Socket clientSock){
        client = clientSock;
        start();
    }
    private static void startConnection() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Which port should be used?");
        int serverPort = Integer.parseInt(br.readLine());
        try(ServerSocket server = new ServerSocket(serverPort, 0)){
            System.out.println("Connection Socket Created");
            while(true){
                try {
                    System.out.println("Waiting for Connection");
                    new Server(server.accept());
                } catch (IOException e) {
                    System.err.println("Accept failed.");
                    System.exit(1);
                }
            }
        }
        catch (IOException e){
            System.err.println("Could not listen on port: "+serverPort);
            System.exit(1);
        }
    }
    @Override
    public void run(){
        System.out.println("New thread started");

        try(
                client;
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))
        ){
            String inputLine;
            while((inputLine = in.readLine())!= null){
                System.out.println(inputLine);
                Request receivedMessage = gson.fromJson(inputLine, Request.class);
                System.out.println(receivedMessage.getMessage());
                System.out.println("Message from" + client.getInetAddress() + ": "+ receivedMessage.getMessage());
                Request sendedMessage = new Request(receivedMessage.getMessage().toUpperCase());
                String responseMessageJson = gson.toJson(sendedMessage);
                out.println(responseMessageJson);
            }
            out.close();
            in.close();
            client.close();
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
