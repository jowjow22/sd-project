package server;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import helpers.singletons.IpAddressesStore;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import records.IpAdressess;
import records.Request;
import records.Response;

import helpers.singletons.IOServerConnection;
import server.routes.Routes;

public class Server extends Thread{
    private final Socket client;
    private final static IpAddressesStore ipAdressessStore = IpAddressesStore.getInstance();
    private final static IpAdresses ipAdresses = new IpAdresses(ipAdressessStore.getIpAdressess());

    
    public static void main(String[] args)   {

        ipAdressessStore.setPanelModel(ipAdresses.getButtonsPanel());
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
        try(ServerSocket server = new ServerSocket(21234, 0)){
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
            System.err.println("Could not listen on port: "+21234);
            System.exit(1);
        }
    }
    @Override
    public void run(){
        ipAdressessStore.addIp(client.getInetAddress().getHostAddress(), String.valueOf(client.getPort()));
        ipAdresses.updateTable(ipAdressessStore.getIpAdressess());
        System.out.println("New Client connected: " + client.getInetAddress().getHostAddress() + " at " + client.getPort() + " port.");

        try(
                client;
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

        ){
            IOServerConnection io = new IOServerConnection(out, in, new IpAdressess(client.getInetAddress().getHostAddress(), String.valueOf(client.getPort())));
            Request<?> request;
            while((request = io.receive(Object.class, in.readLine())) != null){
                Routes routes = new Routes(io);
                routes.receiveRequest(request);
            }
            io.close();
            ipAdressessStore.removeIp(client.getInetAddress().getHostAddress());
            ipAdresses.updateTable(ipAdressessStore.getIpAdressess());
        }
        catch (IOException e){
            System.out.println(e.getMessage());
            ipAdressessStore.removeIp(client.getInetAddress().getHostAddress());
            ipAdresses.updateTable(ipAdressessStore.getIpAdressess());
        }
    }
}
