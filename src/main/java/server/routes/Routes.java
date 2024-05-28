package server.routes;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import enums.Operations;
import enums.Roles;
import enums.Statuses;
import exceptions.EmailAlreadyInUseException;
import helpers.singletons.Database;
import helpers.singletons.IOServerConnection;
import helpers.singletons.Json;
import jakarta.persistence.NoResultException;
import models.Candidate;
import models.Recruiter;
import org.hibernate.exception.ConstraintViolationException;
import records.*;

import java.sql.SQLIntegrityConstraintViolationException;
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

                if (candidateLogin.email() == null || candidateLogin.email().isEmpty() || candidateLogin.password() == null || candidateLogin.password().isEmpty()){
                    Response<CandidateLoginResponse> response = new Response<>(Operations.LOGIN_CANDIDATE, Statuses.INVALID_FIELD);
                    responseMessage(response);
                    return;
                }

                try{
                    Candidate candidate = db.getOneByQuery("SELECT c FROM Candidate c WHERE c.email = '"+candidateLogin.email()+"' AND c.password = '"+candidateLogin.password()+"'", Candidate.class);
                    String token = JWT.create()
                            .withClaim("id", candidate.getId())
                            .withClaim("role", Roles.CANDIDATE.toString())
                            .sign(algorithm);
                    CandidateLoginResponse candidateLoginResponse = new CandidateLoginResponse(token);
                    Response<CandidateLoginResponse> response = new Response<>(Operations.LOGIN_CANDIDATE, Statuses.SUCCESS, candidateLoginResponse);
                    responseMessage(response);
                }
                catch (Exception e){
                    Response<CandidateLoginResponse> response = new Response<>(Operations.LOGIN_CANDIDATE, Statuses.INVALID_LOGIN);
                    responseMessage(response);
                }


            }
            case SIGNUP_CANDIDATE -> {
                CandidateSignUpAndUpdateRequest candidateSignUp = receivedRequest.withDataClass(CandidateSignUpAndUpdateRequest.class).data();
                Candidate candidate = new Candidate();
                candidate.setEmail(candidateSignUp.email());
                candidate.setPassword(candidateSignUp.password());
                candidate.setName(candidateSignUp.name());
                Response<?> response;

                try {
                    db.insert(candidate);
                    response = new Response<CandidateLoginResponse>(Operations.SIGNUP_CANDIDATE, Statuses.SUCCESS);
                }catch (EmailAlreadyInUseException e){
                    response = new Response<CandidateLoginResponse>(Operations.SIGNUP_CANDIDATE, Statuses.USER_EXISTS);
                }
                responseMessage(response);
            }
            case LOOKUP_ACCOUNT_CANDIDATE -> {
                String token = receivedRequest.token();
                try{
                    verifier.verify(token);
                }catch(JWTVerificationException e){
                    Response<Candidate> response = new Response<>(Operations.LOOKUP_ACCOUNT_CANDIDATE, Statuses.INVALID_TOKEN);
                    responseMessage(response);
                }
                try{
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
                }catch(JWTVerificationException e){
                    Response<Candidate> response = new Response<>(Operations.LOOKUP_ACCOUNT_CANDIDATE, Statuses.INVALID_TOKEN);
                    responseMessage(response);
                }
                try{
                    Response<Candidate> response = new Response<>(Operations.LOGOUT_CANDIDATE,Statuses.SUCCESS);
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
                }catch(JWTVerificationException e){
                    Response<Candidate> response = new Response<>(Operations.LOOKUP_ACCOUNT_CANDIDATE, Statuses.INVALID_TOKEN);
                    responseMessage(response);
                }
                try{
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
                CandidateSignUpAndUpdateRequest candidateUpdate = receivedRequest.withDataClass(CandidateSignUpAndUpdateRequest.class).data();
                String token = receivedRequest.token();
                try{
                    verifier.verify(token);
                }catch(JWTVerificationException e){
                    Response<Candidate> response = new Response<>(Operations.LOOKUP_ACCOUNT_CANDIDATE, Statuses.INVALID_TOKEN);
                    responseMessage(response);
                }
                    Map<String, Claim> decoded = JWT.decode(token).getClaims();
                    int id = decoded.get("id").asInt();
                    Candidate candidate = db.selectByPK(Candidate.class, id);
                    if(candidateUpdate.email() != null){
                        candidate.setEmail(candidateUpdate.email());
                    }
                    if(candidateUpdate.password() != null){
                        candidate.setPassword(candidateUpdate.password());
                    }
                    if(candidateUpdate.name() != null){
                        candidate.setName(candidateUpdate.name());
                    }
                    candidate.setId(id);
                    Response<Candidate> response;
                    try{
                        db.update(candidate);
                        response = new Response<>(Operations.UPDATE_ACCOUNT_CANDIDATE, Statuses.SUCCESS);
                    }catch (EmailAlreadyInUseException e){
                        response = new Response<>(Operations.UPDATE_ACCOUNT_CANDIDATE, Statuses.INVALID_EMAIL);
                    }

                    responseMessage(response);
                }
            case SIGNUP_RECRUITER -> {
                RecruiterSignUpAndUpdateRequest candidateSignUp = receivedRequest.withDataClass(RecruiterSignUpAndUpdateRequest.class).data();
                Recruiter recruiter = new Recruiter();
                recruiter.setEmail(candidateSignUp.email());
                recruiter.setPassword(candidateSignUp.password());
                recruiter.setName(candidateSignUp.name());
                recruiter.setIndustry(candidateSignUp.industry());
                recruiter.setDescription(candidateSignUp.description());

                Response<?> response;

                try {
                    db.insert(recruiter);
                    response = new Response<>(Operations.SIGNUP_RECRUITER, Statuses.SUCCESS);
                }catch (EmailAlreadyInUseException e){
                    response = new Response<>(Operations.SIGNUP_RECRUITER, Statuses.USER_EXISTS);
                }
                responseMessage(response);
            }
            case LOGIN_RECRUITER -> {
                RecruiterLoginRequest recruiterLogin =  receivedRequest.withDataClass(RecruiterLoginRequest.class).data();

                if (recruiterLogin.email() == null || recruiterLogin.email().isEmpty() || recruiterLogin.password() == null || recruiterLogin.password().isEmpty()){
                    Response<?> response = new Response(Operations.LOGIN_RECRUITER, Statuses.INVALID_FIELD);
                    responseMessage(response);
                    return;
                }

                try{
                    Recruiter recruiter = db.getOneByQuery("SELECT r FROM Recruiter r WHERE r.email = '"+recruiterLogin.email()+"' AND r.password = '"+recruiterLogin.password()+"'", Recruiter.class);
                    String token = JWT.create()
                            .withClaim("id", recruiter.getId())
                            .withClaim("role", Roles.RECRUITER.toString())
                            .sign(algorithm);
                    RecruiterLoginResponse recruiterLoginResponse = new RecruiterLoginResponse(token);
                    Response<RecruiterLoginResponse> response = new Response<>(Operations.LOGIN_RECRUITER, Statuses.SUCCESS, recruiterLoginResponse);
                    responseMessage(response);
                }
                catch (Exception e){
                    Response<RecruiterLoginResponse> response = new Response<>(Operations.LOGIN_RECRUITER, Statuses.INVALID_LOGIN);
                    responseMessage(response);
                }
            }
            }

    }
}
