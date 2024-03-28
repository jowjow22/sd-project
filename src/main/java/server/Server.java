package server;

import java.io.*;
import java.net.*;

public class Server extends Thread{
    private static final Socket client;
    public static void main(String[] args) throws IOException  {
        InetAddress ip = InetAddress.getByName("127.0.0.1");
        try(ServerSocket server = new ServerSocket(10007, 0, ip);){
            while(true){
                try {
                    System.out.println("Waiting for Connection");
                    server.accept();
                } catch (IOException e) {
                    System.err.println("Accept failed.");
                    System.exit(1);
                }
            }
        }
        catch (IOException e){
            System.err.println(e);
        }
    }
    @Override
    public void run(){
        System.out.println("New thread started");

        try(
                client;
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        ){
            String inputLine;
            while((inputLine = in.readLine())!= null){
                System.out.println("Message from" + client.getInetAddress() + ": "+ inputLine);

            }
            out.close();
            in.close();
            client.close();
        }
        catch (IOException e){

        }
    }
}
