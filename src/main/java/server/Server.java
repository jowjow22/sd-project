package server;
import java.io.*;
import java.net.*;

import com.google.gson.internal.LinkedTreeMap;
import enums.Operations;
import enums.Statuses;
import helpers.Json;
import records.*;

public class Server extends Thread{
    private final Socket client;
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
            String request;
            while((request = in.readLine()) != null){

                Json json = Json.getInstance();
                Request<?> clientRequest = json.fromJson(request, Request.class);
                Operations operation = clientRequest.operation();

                Response<?> response;

                switch(operation){
                    case LOGIN_CANDIDATE -> {
                        System.out.println("\n[LOG]: Requested Operation: candidate login.");
                        LinkedTreeMap<String, ?> data = (LinkedTreeMap<String, ?>) clientRequest.data();
                        System.out.println("[LOG]: Email: " + data.get("email"));
                        System.out.println("[LOG]: Password: " + data.get("password"));

                        CandidateLoginResponse responseModel = new CandidateLoginResponse("algumacoisa");
                        response = new Response<>(operation, Statuses.SUCCESS, responseModel);
                        String jsonResponse = json.toJson(response);

                        System.out.println("[LOG]: SENDING RESPONSE: " + jsonResponse);
                        out.println(jsonResponse);
                    }
                    case SIGNUP_CANDIDATE -> {
                        System.out.println("\n[LOG]: Requested Operation: candidate sign up.");
                        LinkedTreeMap<String, ?> data = (LinkedTreeMap<String, ?>) clientRequest.data();
                        System.out.println("[LOG]: Email: " + data.get("email"));
                        System.out.println("[LOG]: Password: " + data.get("password"));
                        System.out.println("[LOG]: Name: " + data.get("name"));

                        CandidateSignUpResponse responseModel = new CandidateSignUpResponse();
                        response = new Response<>(operation, Statuses.SUCCESS, responseModel);
                        String jsonResponse = json.toJson(response);

                        System.out.println("[LOG]: SENDING RESPONSE: " + jsonResponse);
                        out.println(jsonResponse);
                    }
                    case LOGOUT_CANDIDATE -> {
                        System.out.println("\n[LOG]: Requested Operation: candidate logout.");
                        LinkedTreeMap<String, ?> data = (LinkedTreeMap<String, ?>) clientRequest.data();
                        System.out.println("[LOG]: Token: " + data.get("token"));

                        CandidateLogoutResponse responseModel = new CandidateLogoutResponse();
                        response = new Response<>(operation, Statuses.SUCCESS, responseModel);
                        String jsonResponse = json.toJson(response);

                        System.out.println("[LOG]: SENDING RESPONSE: " + jsonResponse);
                        out.println(jsonResponse);
                    }
                    case LOOKUP_ACCOUNT_CANDIDATE -> {
                        System.out.println("\n[LOG]: Requested Operation: candidate look up.");
                        LinkedTreeMap<String, ?> data = (LinkedTreeMap<String, ?>) clientRequest.data();
                        System.out.println("[LOG]: Token: " + data.get("token"));

                        CandidateLookupResponse responseModel = new CandidateLookupResponse("alo", "bom dia", "algumacoisa");
                        response = new Response<>(operation, Statuses.SUCCESS, responseModel);
                        String jsonResponse = json.toJson(response);

                        System.out.println("[LOG]: SENDING RESPONSE: " + jsonResponse);
                        out.println(jsonResponse);
                    }
                    case UPDATE_ACCOUNT_CANDIDATE -> {
                        System.out.println("\n[LOG]: Requested Operation: candidate update.");
                        LinkedTreeMap<String, ?> data = (LinkedTreeMap<String, ?>) clientRequest.data();
                        System.out.println("[LOG]: Email: " + data.get("email"));
                        System.out.println("[LOG]: Password: " + data.get("password"));
                        System.out.println("[LOG]: Name: " + data.get("name"));

                        CandidateUpdateResponse responseModel = new CandidateUpdateResponse();
                        response = new Response<>(operation, Statuses.SUCCESS, responseModel);
                        String jsonResponse = json.toJson(response);

                        System.out.println("[LOG]: SENDING RESPONSE: " + jsonResponse);
                        out.println(jsonResponse);
                    }
                }
            }
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
