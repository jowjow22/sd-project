package server.routes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import enums.Operations;
import records.CandidateLoginRequest;
import records.CandidateLoginResponse;
import records.Request;
import records.Response;

import java.io.PrintWriter;

public class Routes {
    private PrintWriter out;
    private final Gson gson = new GsonBuilder()
            .create();
    public Routes(PrintWriter out){
        this.out = out;
    }
    public void responseMessage(Response toSendResponse){
        String responseMessageJson = gson.toJson(toSendResponse);
        out.println(responseMessageJson);
    }
    public void receiveRequest(String receivedRequest){
        Request receivedMessage = gson.fromJson(receivedRequest, Request.class);

        switch (receivedMessage.getOperation()){
            case LOGIN_CANDIDATE -> {
                LinkedTreeMap data = (LinkedTreeMap) receivedMessage.getData();
                String toJson = gson.toJson(data);
                CandidateLoginRequest candidateLogin = gson.fromJson(toJson, CandidateLoginRequest.class);
                System.out.println("Email: " + candidateLogin.email());
                System.out.println("Password: " + candidateLogin.password());

                Response<CandidateLoginResponse> response = new Response<CandidateLoginResponse>(Operations.LOGIN_CANDIDATE, new CandidateLoginResponse("token"));
                responseMessage(response);
            }
        }
    }
}
