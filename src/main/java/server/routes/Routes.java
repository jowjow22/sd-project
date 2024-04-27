package server.routes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import enums.Operations;
import enums.Statuses;
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
    public void responseMessage(Response<?> toSendResponse){
        String responseMessageJson = gson.toJson(toSendResponse);
        out.println(responseMessageJson);
    }
    public void receiveRequest(String receivedRequest){
        Request<CandidateLoginRequest> receivedMessage = gson.fromJson(receivedRequest, Request.class);

        switch (receivedMessage.operation()){
            case LOGIN_CANDIDATE -> {
                CandidateLoginRequest candidateLogin = receivedMessage.data(CandidateLoginRequest.class);
                System.out.println("Email: " + candidateLogin.email());
                System.out.println("Password: " + candidateLogin.password());

                Response<CandidateLoginResponse> response = new Response<CandidateLoginResponse>(Operations.LOGIN_CANDIDATE, Statuses.SUCCESS,new CandidateLoginResponse("token"));
                responseMessage(response);
            }
            case SIGNUP_CANDIDATE -> {
                System.out.println("Signup candidate");
                Response<CandidateLoginResponse> response = new Response<CandidateLoginResponse>(Operations.SIGNUP_CANDIDATE, Statuses.SUCCESS,new CandidateLoginResponse("token"));
                responseMessage(response);
            }
        }
    }
}
