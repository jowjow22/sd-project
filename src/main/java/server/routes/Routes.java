package server.routes;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import enums.Operations;
import enums.Roles;
import enums.Statuses;
import helpers.singletons.Database;
import helpers.singletons.IOServerConnection;
import helpers.singletons.Json;
import models.Candidate;
import records.*;
import java.util.Map;

public class Routes {
    private final Json json = Json.getInstance();
    private  final Database db = Database.getInstance();
    private final Algorithm algorithm = Algorithm.HMAC256("DISTRIBUIDOS");
    private final IOServerConnection io;
    private final JWTVerifier verifier = JWT.require(algorithm)
            .build();
    public Routes(IOServerConnection io){
        this.io = io;
    }
    public void responseMessage(Response<?> toSendResponse){
        io.send(toSendResponse);
    }
    public void receiveRequest(Request<?> receivedRequest){

        switch (receivedRequest.operation()){
            case LOGIN_CANDIDATE -> {
                CandidateLoginRequest candidateLogin =  receivedRequest.withDataClass(CandidateLoginRequest.class).data();

                try{
                    Candidate candidate = db.getOneByQuery("SELECT c FROM Candidate c WHERE c.email = '"+candidateLogin.email()+"' AND c.password = '"+candidateLogin.password()+"'", Candidate.class);
                    String token = JWT.create()
                            .withClaim("id", candidate.getId())
                            .withClaim("role", Roles.CANDIDATE.toString())
                            .sign(algorithm);
                    Response<CandidateLoginResponse> response = new Response<CandidateLoginResponse>(Operations.LOGIN_CANDIDATE, Statuses.SUCCESS, token);
                    responseMessage(response);
                }
                catch (Exception e){
                    Response<CandidateLoginResponse> response = new Response<>(Operations.LOGIN_CANDIDATE, Statuses.USER_NOT_FOUND);
                    responseMessage(response);
                }


            }
            case SIGNUP_CANDIDATE -> {
                CandidateSignUpRequest candidateSignUp = receivedRequest.withDataClass(CandidateSignUpRequest.class).data();
                Candidate candidate = new Candidate();
                candidate.setEmail(candidateSignUp.email());
                candidate.setPassword(candidateSignUp.password());
                candidate.setName(candidateSignUp.name());

                db.insert(candidate);

                Response<CandidateLoginResponse> response = new Response<CandidateLoginResponse>(Operations.SIGNUP_CANDIDATE, Statuses.SUCCESS);
                responseMessage(response);
            }
            case LOOKUP_ACCOUNT_CANDIDATE -> {
                String token = receivedRequest.token();
                try{
                    verifier.verify(token);
                    Map<String, Claim> decoded = JWT.decode(token).getClaims();
                    int id = decoded.get("id").asInt();
                    Candidate candidate = db.getOneByQuery("SELECT c FROM Candidate c WHERE c.id = "+id, Candidate.class);
                    Response<Candidate> response = new Response<>(Operations.LOOKUP_ACCOUNT_CANDIDATE, Statuses.SUCCESS, candidate);
                    responseMessage(response);
                }
                catch (Exception e){
                    Response<Candidate> response = new Response<>(Operations.LOOKUP_ACCOUNT_CANDIDATE, Statuses.USER_NOT_FOUND);
                    responseMessage(response);
                }
            }
            case LOGOUT_CANDIDATE -> {
                String token = receivedRequest.token();
                try{
                    verifier.verify(token);
                    Response<Candidate> response = new Response<>(Operations.LOGOUT_CANDIDATE,Statuses.SUCCESS, token);
                    responseMessage(response);
                }
                catch (Exception e){
                    Response<Candidate> response = new Response<>(Operations.LOGOUT_CANDIDATE, Statuses.USER_NOT_FOUND);
                    responseMessage(response);
                }
            }
            case DELETE_ACCOUNT_CANDIDATE -> {
                String token = receivedRequest.token();
                try{
                    verifier.verify(token);
                    Map<String, Claim> decoded = JWT.decode(token).getClaims();
                    int id = decoded.get("id").asInt();
                    Candidate candidate = db.selectByPK(Candidate.class, id);
                    db.delete(candidate);
                    Response<Candidate> response = new Response<>(Operations.DELETE_ACCOUNT_CANDIDATE, Statuses.SUCCESS);
                    responseMessage(response);
                }
                catch (Exception e){
                    Response<Candidate> response = new Response<>(Operations.DELETE_ACCOUNT_CANDIDATE, Statuses.USER_NOT_FOUND);
                    responseMessage(response);
                }
            }
            case UPDATE_ACCOUNT_CANDIDATE -> {
                CandidateSignUpRequest candidateSignUp = receivedRequest.withDataClass(CandidateSignUpRequest.class).data();
                String token = receivedRequest.token();
                try{
                    verifier.verify(token);
                    Map<String, Claim> decoded = JWT.decode(token).getClaims();
                    int id = decoded.get("id").asInt();
                    Candidate candidate = db.selectByPK(Candidate.class, id);
                    candidate.setEmail(candidateSignUp.email());
                    candidate.setPassword(candidateSignUp.password());
                    candidate.setName(candidateSignUp.name());
                    candidate.setId(id);
                    db.update(candidate);
                    Response<Candidate> response = new Response<>(Operations.UPDATE_ACCOUNT_CANDIDATE, Statuses.SUCCESS);
                    responseMessage(response);
                }
                catch (Exception e){
                    Response<Candidate> response = new Response<>(Operations.UPDATE_ACCOUNT_CANDIDATE, Statuses.USER_NOT_FOUND);
                    responseMessage(response);
                }
            }
        }
    }
}
