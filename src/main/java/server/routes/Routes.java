package server.routes;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import enums.Operations;
import enums.Roles;
import enums.Statuses;
import helpers.singletons.Database;
import helpers.singletons.Json;
import models.Candidate;
import records.*;

import java.io.PrintWriter;
import java.util.Map;

public class Routes {
    private PrintWriter out;
    private final Json json = Json.getInstance();
    private  final Database db = Database.getInstance();
    private final Algorithm algorithm = Algorithm.HMAC256("DISTRIBUIDOS");
    private final JWTVerifier verifier = JWT.require(algorithm)
            .build();
    public Routes(PrintWriter out){
        this.out = out;
    }
    public void responseMessage(Response<?> toSendResponse){
        String responseMessageJson = json.toJson(toSendResponse);
        System.out.println(responseMessageJson);
        out.println(responseMessageJson);
    }
    public void receiveRequest(String receivedRequest){
        Request receivedMessage = json.fromJson(receivedRequest, Request.class);

        switch (receivedMessage.operation()){
            case LOGIN_CANDIDATE -> {
                CandidateLoginRequest candidateLogin = (CandidateLoginRequest) receivedMessage.data(CandidateLoginRequest.class);

                try{
                    Candidate candidate = db.getOneByQuery("SELECT c FROM Candidate c WHERE c.email = '"+candidateLogin.email()+"' AND c.password = '"+candidateLogin.password()+"'", Candidate.class);
                    String token = JWT.create()
                            .withClaim("id", candidate.getId())
                            .withClaim("role", Roles.CANDIDATE.toString())
                            .sign(algorithm);
                    Response<CandidateLoginResponse> response = new Response<CandidateLoginResponse>(Operations.LOGIN_CANDIDATE, Statuses.SUCCESS,new CandidateLoginResponse(token));
                    responseMessage(response);
                }
                catch (Exception e){
                    Response<CandidateLoginResponse> response = new Response<>(Operations.LOGIN_CANDIDATE, Statuses.USER_NOT_FOUND);
                    responseMessage(response);
                }


            }
            case SIGNUP_CANDIDATE -> {
                CandidateSignUpRequest candidateSignUp = (CandidateSignUpRequest) receivedMessage.data(CandidateSignUpRequest.class);
                Candidate candidate = new Candidate();
                candidate.setEmail(candidateSignUp.email());
                candidate.setPassword(candidateSignUp.password());
                candidate.setName(candidateSignUp.name());

                db.insert(candidate);

                Response<CandidateLoginResponse> response = new Response<CandidateLoginResponse>(Operations.SIGNUP_CANDIDATE, Statuses.SUCCESS);
                responseMessage(response);
            }
            case LOOKUP_ACCOUNT_CANDIDATE -> {
                String token = receivedMessage.token();
                try{
                    verifier.verify(token);
                    Map<String, Claim> decoded = JWT.decode(token).getClaims();
                    int id = decoded.get("id").asInt();
                    Candidate candidate = db.getOneByQuery("SELECT c FROM Candidate c WHERE c.id = "+id, Candidate.class);
                    Response<Candidate> response = new Response<Candidate>(Operations.LOOKUP_ACCOUNT_CANDIDATE, Statuses.SUCCESS, candidate);
                    responseMessage(response);
                }
                catch (Exception e){
                    Response<Candidate> response = new Response<Candidate>(Operations.LOOKUP_ACCOUNT_CANDIDATE, Statuses.USER_NOT_FOUND);
                    responseMessage(response);
                }
            }
        }
    }
}
