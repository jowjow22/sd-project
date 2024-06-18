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
import models.*;
import org.hibernate.exception.ConstraintViolationException;
import records.*;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
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
        // insert the skills
        /*
        NodeJs	JavaScript	Java	C	HTML	CSS
React	ReactNative	TypeScript	Ruby
         */
        try {
            db.insert(new Skill("NodeJs"));
            db.insert(new Skill("JavaScript"));
            db.insert(new Skill("Java"));
            db.insert(new Skill("C"));
            db.insert(new Skill("HTML"));
            db.insert(new Skill("CSS"));
            db.insert(new Skill("React"));
            db.insert(new Skill("ReactNative"));
            db.insert(new Skill("TypeScript"));
            db.insert(new Skill("Ruby"));
        }
        catch (Exception e){
            System.out.println("Skills already inserted");
        }
    }
    public void responseMessage(Response<?> toSendResponse){
        io.send(toSendResponse);
    }
    public void receiveRequest(Request<?> receivedRequest){

        switch (receivedRequest.operation()) {
            case LOGIN_CANDIDATE -> {
                CandidateLoginRequest candidateLogin = receivedRequest.withDataClass(CandidateLoginRequest.class).data();

                if (candidateLogin.email() == null || candidateLogin.email().isEmpty() || candidateLogin.password() == null || candidateLogin.password().isEmpty()) {
                    Response<CandidateLoginResponse> response = new Response<>(Operations.LOGIN_CANDIDATE, Statuses.INVALID_FIELD);
                    responseMessage(response);
                    return;
                }

                try {
                    Candidate candidate = db.getOneByQuery("SELECT c FROM Candidate c WHERE c.email = '" + candidateLogin.email() + "' AND c.password = '" + candidateLogin.password() + "'", Candidate.class);
                    String token = JWT.create()
                            .withClaim("id", candidate.getId())
                            .withClaim("role", Roles.CANDIDATE.toString())
                            .sign(algorithm);
                    System.out.println(token);
                    CandidateLoginResponse candidateLoginResponse = new CandidateLoginResponse(token);
                    Response<CandidateLoginResponse> response = new Response<>(Operations.LOGIN_CANDIDATE, Statuses.SUCCESS, candidateLoginResponse);
                    responseMessage(response);

                } catch (Exception e) {
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
                } catch (EmailAlreadyInUseException e) {
                    response = new Response<CandidateLoginResponse>(Operations.SIGNUP_CANDIDATE, Statuses.USER_EXISTS);
                }
                responseMessage(response);
            }
            case LOOKUP_ACCOUNT_CANDIDATE -> {
                String token = receivedRequest.token();
                try {
                    verifier.verify(token);
                } catch (JWTVerificationException e) {
                    Response<?> response = new Response<>(Operations.LOOKUP_ACCOUNT_CANDIDATE, Statuses.INVALID_TOKEN);
                    responseMessage(response);
                }
                try {
                    Map<String, Claim> decoded = JWT.decode(token).getClaims();
                    int id = decoded.get("id").asInt();
                    Candidate candidate = db.getOneByQuery("SELECT c FROM Candidate c WHERE c.id = " + id, Candidate.class);
                    CandidateResponse candidateResponse = new CandidateResponse(candidate);
                    Response<CandidateResponse> response = new Response<>(Operations.LOOKUP_ACCOUNT_CANDIDATE, Statuses.SUCCESS, candidateResponse);
                    responseMessage(response);
                } catch (Exception e) {
                    Response<Candidate> response = new Response<>(Operations.LOOKUP_ACCOUNT_CANDIDATE, Statuses.USER_NOT_FOUND);
                    responseMessage(response);
                }
            }
            case LOGOUT_CANDIDATE -> {
                String token = receivedRequest.token();
                try {
                    verifier.verify(token);
                } catch (JWTVerificationException e) {
                    Response<Candidate> response = new Response<>(Operations.LOGOUT_CANDIDATE, Statuses.INVALID_TOKEN);
                    responseMessage(response);
                }
                try {
                    Response<Candidate> response = new Response<>(Operations.LOGOUT_CANDIDATE, Statuses.SUCCESS);
                    responseMessage(response);
                } catch (Exception e) {
                    Response<Candidate> response = new Response<>(Operations.LOGOUT_CANDIDATE, Statuses.USER_NOT_FOUND);
                    responseMessage(response);
                }
            }
            case DELETE_ACCOUNT_CANDIDATE -> {
                String token = receivedRequest.token();
                try {
                    verifier.verify(token);
                } catch (JWTVerificationException e) {
                    Response<Candidate> response = new Response<>(Operations.LOOKUP_ACCOUNT_CANDIDATE, Statuses.INVALID_TOKEN);
                    responseMessage(response);
                }
                try {
                    Map<String, Claim> decoded = JWT.decode(token).getClaims();
                    int id = decoded.get("id").asInt();
                    Candidate candidate = db.selectByPK(Candidate.class, id);
                    db.delete(candidate);
                    Response<Candidate> response = new Response<>(Operations.DELETE_ACCOUNT_CANDIDATE, Statuses.SUCCESS);
                    responseMessage(response);
                } catch (Exception e) {
                    Response<Candidate> response = new Response<>(Operations.DELETE_ACCOUNT_CANDIDATE, Statuses.USER_NOT_FOUND);
                    responseMessage(response);
                }
            }
            case UPDATE_ACCOUNT_CANDIDATE -> {
                CandidateSignUpAndUpdateRequest candidateUpdate = receivedRequest.withDataClass(CandidateSignUpAndUpdateRequest.class).data();
                String token = receivedRequest.token();
                try {
                    verifier.verify(token);
                } catch (JWTVerificationException e) {
                    Response<Candidate> response = new Response<>(Operations.LOOKUP_ACCOUNT_CANDIDATE, Statuses.INVALID_TOKEN);
                    responseMessage(response);
                }
                Map<String, Claim> decoded = JWT.decode(token).getClaims();
                int id = decoded.get("id").asInt();
                Candidate candidate = db.selectByPK(Candidate.class, id);
                if (candidateUpdate.email() != null) {
                    candidate.setEmail(candidateUpdate.email());
                }
                if (candidateUpdate.password() != null) {
                    candidate.setPassword(candidateUpdate.password());
                }
                if (candidateUpdate.name() != null) {
                    candidate.setName(candidateUpdate.name());
                }
                candidate.setId(id);
                Response<Candidate> response;
                try {
                    db.update(candidate);
                    response = new Response<>(Operations.UPDATE_ACCOUNT_CANDIDATE, Statuses.SUCCESS);
                } catch (EmailAlreadyInUseException e) {
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
                } catch (EmailAlreadyInUseException e) {
                    response = new Response<>(Operations.SIGNUP_RECRUITER, Statuses.USER_EXISTS);
                }
                responseMessage(response);
            }
            case LOGOUT_RECRUITER -> {
                String token = receivedRequest.token();
                try {
                    verifier.verify(token);
                } catch (JWTVerificationException e) {
                    Response<Recruiter> response = new Response<>(Operations.LOGOUT_RECRUITER, Statuses.INVALID_TOKEN);
                    responseMessage(response);
                }
                try {
                    Response<Recruiter> response = new Response<>(Operations.LOGOUT_RECRUITER, Statuses.SUCCESS);
                    responseMessage(response);
                } catch (Exception e) {
                    Response<Recruiter> response = new Response<>(Operations.LOGOUT_RECRUITER, Statuses.USER_NOT_FOUND);
                    responseMessage(response);
                }
            }
            case LOOKUP_ACCOUNT_RECRUITER -> {
                String token = receivedRequest.token();
                try {
                    verifier.verify(token);
                } catch (JWTVerificationException e) {
                    Response<Recruiter> response = new Response<>(Operations.LOOKUP_ACCOUNT_RECRUITER, Statuses.INVALID_TOKEN);
                    responseMessage(response);
                }
                try {
                    Map<String, Claim> decoded = JWT.decode(token).getClaims();
                    int id = decoded.get("id").asInt();
                    Recruiter recruiter = db.getOneByQuery("SELECT r FROM Recruiter r WHERE r.id = " + id, Recruiter.class);
                    Response<Recruiter> response = new Response<>(Operations.LOOKUP_ACCOUNT_RECRUITER, Statuses.SUCCESS, recruiter);
                    responseMessage(response);
                } catch (Exception e) {
                    Response<Recruiter> response = new Response<>(Operations.LOOKUP_ACCOUNT_RECRUITER, Statuses.USER_NOT_FOUND);
                    responseMessage(response);
                }
            }
            case DELETE_ACCOUNT_RECRUITER -> {
                String token = receivedRequest.token();
                try {
                    verifier.verify(token);
                } catch (JWTVerificationException e) {
                    Response<Recruiter> response = new Response<>(Operations.LOOKUP_ACCOUNT_RECRUITER, Statuses.INVALID_TOKEN);
                    responseMessage(response);
                }
                try {
                    Map<String, Claim> decoded = JWT.decode(token).getClaims();
                    int id = decoded.get("id").asInt();
                    Recruiter recruiter = db.selectByPK(Recruiter.class, id);
                    db.delete(recruiter);
                    Response<Recruiter> response = new Response<>(Operations.DELETE_ACCOUNT_RECRUITER, Statuses.SUCCESS);
                    responseMessage(response);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    Response<Recruiter> response = new Response<>(Operations.DELETE_ACCOUNT_RECRUITER, Statuses.USER_NOT_FOUND);
                    responseMessage(response);
                }
            }
            case UPDATE_ACCOUNT_RECRUITER -> {
                RecruiterSignUpAndUpdateRequest recruiterUpdate = receivedRequest.withDataClass(RecruiterSignUpAndUpdateRequest.class).data();
                String token = receivedRequest.token();
                try {
                    verifier.verify(token);
                } catch (JWTVerificationException e) {
                    Response<Recruiter> response = new Response<>(Operations.LOOKUP_ACCOUNT_RECRUITER, Statuses.INVALID_TOKEN);
                    responseMessage(response);
                }
                Map<String, Claim> decoded = JWT.decode(token).getClaims();
                int id = decoded.get("id").asInt();
                Recruiter recruiter = db.selectByPK(Recruiter.class, id);
                if (recruiterUpdate.email() != null) {
                    recruiter.setEmail(recruiterUpdate.email());
                }
                if (recruiterUpdate.password() != null) {
                    recruiter.setPassword(recruiterUpdate.password());
                }
                if (recruiterUpdate.name() != null) {
                    recruiter.setName(recruiterUpdate.name());
                }
                if (recruiterUpdate.industry() != null) {
                    recruiter.setIndustry(recruiterUpdate.industry());
                }
                if (recruiterUpdate.description() != null) {
                    recruiter.setDescription(recruiterUpdate.description());
                }
                recruiter.setId(id);
                Response<Recruiter> response;
                try {
                    db.update(recruiter);
                    response = new Response<>(Operations.UPDATE_ACCOUNT_RECRUITER, Statuses.SUCCESS);
                } catch (EmailAlreadyInUseException e) {
                    response = new Response<>(Operations.UPDATE_ACCOUNT_RECRUITER, Statuses.INVALID_EMAIL);
                }

                responseMessage(response);
            }
            case LOGIN_RECRUITER -> {
                RecruiterLoginRequest recruiterLogin = receivedRequest.withDataClass(RecruiterLoginRequest.class).data();

                if (recruiterLogin.email() == null || recruiterLogin.email().isEmpty() || recruiterLogin.password() == null || recruiterLogin.password().isEmpty()) {
                    Response<?> response = new Response<>(Operations.LOGIN_RECRUITER, Statuses.INVALID_FIELD);
                    responseMessage(response);
                    return;
                }

                try {
                    Recruiter recruiter = db.getOneByQuery("SELECT r FROM Recruiter r WHERE r.email = '" + recruiterLogin.email() + "' AND r.password = '" + recruiterLogin.password() + "'", Recruiter.class);
                    String token = JWT.create()
                            .withClaim("id", recruiter.getId())
                            .withClaim("role", Roles.RECRUITER.toString())
                            .sign(algorithm);
                    RecruiterLoginResponse recruiterLoginResponse = new RecruiterLoginResponse(token);
                    Response<RecruiterLoginResponse> response = new Response<>(Operations.LOGIN_RECRUITER, Statuses.SUCCESS, recruiterLoginResponse);
                    responseMessage(response);
                } catch (Exception e) {
                    Response<RecruiterLoginResponse> response = new Response<>(Operations.LOGIN_RECRUITER, Statuses.INVALID_LOGIN);
                    responseMessage(response);
                }
            }
            case INCLUDE_SKILL -> {
                IncludeSkillRequest includeSkillRequest = receivedRequest.withDataClass(IncludeSkillRequest.class).data();
                String token = receivedRequest.token();
                try {
                    verifier.verify(token);
                } catch (JWTVerificationException e) {
                    Response<?> response = new Response<>(Operations.INCLUDE_SKILL, Statuses.INVALID_TOKEN);
                    responseMessage(response);
                }
                try {
                    Map<String, Claim> decoded = JWT.decode(token).getClaims();
                    int id = decoded.get("id").asInt();
                    Candidate candidate = db.selectByPK(Candidate.class, id);
                    Skill skill;
                    try {
                        skill = db.getOneByQuery("SELECT s FROM Skill s WHERE s.skill = '" + includeSkillRequest.skill() + "'", Skill.class);
                    } catch (NoResultException e) {
                        Response<?> response = new Response<>(Operations.INCLUDE_SKILL, Statuses.SKILL_NOT_FOUND);
                        responseMessage(response);
                        return;
                    }
                    try {
                        List<Experience> candidateExperience = candidate.getExperiences();
                        for (Experience experience : candidateExperience) {
                            if (experience.getSkill().getId() == skill.getId()) {
                                Response<?> response = new Response<>(Operations.INCLUDE_SKILL, Statuses.SKILL_EXISTS);
                                responseMessage(response);
                                return;
                            }
                        }
                    } catch (NoResultException e) {
                        // do nothing
                    }
                    Experience experience = new Experience();
                    experience.setCandidate(candidate);
                    experience.setSkill(skill);
                    experience.setYears(Integer.parseInt(includeSkillRequest.experience()));
                    candidate.getExperiences().add(experience);
                    db.update(candidate);
                    Response<?> response = new Response<>(Operations.INCLUDE_SKILL, Statuses.SUCCESS);
                    responseMessage(response);
                } catch (Exception e) {
                    System.out.println(e);
                    Response<?> response = new Response<>(Operations.INCLUDE_SKILL, Statuses.ERROR);
                    responseMessage(response);
                }
            }
            case LOOKUP_SKILL -> {
                String token = receivedRequest.token();
                try {
                    verifier.verify(token);
                } catch (JWTVerificationException e) {
                    Response<?> response = new Response<>(Operations.LOOKUP_SKILL, Statuses.INVALID_TOKEN);
                    responseMessage(response);
                }
                try {
                    LookUpSkillRequest skillRequest = receivedRequest.withDataClass(LookUpSkillRequest.class).data();
                    Candidate candidate = db.getOneByQuery("SELECT c FROM Candidate c WHERE c.id = " + JWT.decode(token).getClaims().get("id").asInt(), Candidate.class);

                    List<Experience> experiences = candidate.getExperiences();
                    Experience experienceToShow = null;
                    for (Experience experience : experiences) {
                        if (experience.getSkill().getSkill().equals(skillRequest.skill())) {
                            experienceToShow = experience;
                        }
                    }
                    if (experienceToShow == null) {
                        Response<?> response = new Response<>(Operations.LOOKUP_SKILL, Statuses.SKILL_NOT_FOUND);
                        responseMessage(response);
                    }
                    ExperienceToResponse infos = new ExperienceToResponse(experienceToShow.getSkill().getSkill(), experienceToShow.getYears().toString());
                    Response<ExperienceToResponse> response = new Response<>(Operations.LOOKUP_SKILL, Statuses.SUCCESS, infos);
                    responseMessage(response);
                } catch (Exception e) {
                    Response<?> response = new Response<>(Operations.LOOKUP_SKILL, Statuses.ERROR);
                    responseMessage(response);
                }
            }
            case LOOKUP_SKILLSET -> {
                String token = receivedRequest.token();
                try {
                    verifier.verify(token);
                } catch (JWTVerificationException e) {
                    Response<?> response = new Response<>(Operations.LOOKUP_SKILLSET, Statuses.INVALID_TOKEN);
                    responseMessage(response);
                }
                try {
                    LookUpSkillRequest skillRequest = receivedRequest.withDataClass(LookUpSkillRequest.class).data();
                    Candidate candidate = db.getOneByQuery("SELECT c FROM Candidate c WHERE c.id = " + JWT.decode(token).getClaims().get("id").asInt(), Candidate.class);

                    List<Experience> experiences = candidate.getExperiences();
                    List<ExperienceToResponse> skillset = new ArrayList<>();
                    for (Experience experience : experiences) {
                        skillset.add(new ExperienceToResponse(experience.getSkill().getSkill(), experience.getYears().toString()));
                    }
                    SkillSetResponse skillSetResponse = new SkillSetResponse(skillset.size(), skillset);
                    Response<SkillSetResponse> response = new Response<>(Operations.LOOKUP_SKILLSET, Statuses.SUCCESS, skillSetResponse);
                    responseMessage(response);
                } catch (Exception e) {
                    Response<?> response = new Response<>(Operations.LOOKUP_SKILLSET, Statuses.ERROR);
                    responseMessage(response);
                }
            }
            case UPDATE_SKILL -> {
                String token = receivedRequest.token();
                try {
                    verifier.verify(token);
                } catch (JWTVerificationException e) {
                    Response<?> response = new Response<>(Operations.UPDATE_SKILL, Statuses.INVALID_TOKEN);
                    responseMessage(response);
                }

                try {
                    UpdateSkillRequest updateSkillRequest = receivedRequest.withDataClass(UpdateSkillRequest.class).data();
                    Candidate candidate = db.getOneByQuery("SELECT c FROM Candidate c WHERE c.id = " + JWT.decode(token).getClaims().get("id").asInt(), Candidate.class);
                    List<Experience> experiences = candidate.getExperiences();
                    for (Experience experience : experiences) {
                        if (experience.getSkill().getSkill().equals(updateSkillRequest.skill())) {
                            experience.setYears(updateSkillRequest.experience());
                            Skill skill = null;
                            try {
                                skill = db.getOneByQuery("SELECT s FROM Skill s WHERE s.skill = '" + updateSkillRequest.newSkill() + "'", Skill.class);
                            } catch (NoResultException e) {
                                Response<?> response = new Response<>(Operations.UPDATE_SKILL, Statuses.SKILL_NOT_FOUND);
                                responseMessage(response);
                            }
                            experience.setSkill(skill);
                            db.update(experience);
                            Response<?> response = new Response<>(Operations.UPDATE_SKILL, Statuses.SUCCESS);
                            responseMessage(response);
                            return;
                        }
                    }
                } catch (Exception e) {
                    Response<?> response = new Response<>(Operations.UPDATE_SKILL, Statuses.ERROR);
                    responseMessage(response);
                }
            }
            case DELETE_SKILL -> {
                String token = receivedRequest.token();
                try {
                    verifier.verify(token);
                } catch (JWTVerificationException e) {
                    Response<?> response = new Response<>(Operations.DELETE_SKILL, Statuses.INVALID_TOKEN);
                    responseMessage(response);
                }

                try {
                    DeleteSkillRequest deleteSkillRequest = receivedRequest.withDataClass(DeleteSkillRequest.class).data();
                    Candidate candidate = db.getOneByQuery("SELECT c FROM Candidate c WHERE c.id = " + JWT.decode(token).getClaims().get("id").asInt(), Candidate.class);
                    try {
                        Skill skill = db.getOneByQuery("SELECT s FROM Skill s WHERE s.skill = '" + deleteSkillRequest.skill() + "'", Skill.class);
                    } catch (NoResultException e) {
                        Response<?> response = new Response<>(Operations.DELETE_SKILL, Statuses.SKILL_NOT_FOUND);
                        responseMessage(response);
                    }
                    List<Experience> experiences = candidate.getExperiences();
                    for (Experience experience : experiences) {
                        if (experience.getSkill().getSkill().equals(deleteSkillRequest.skill())) {
                            db.delete(experience);
                            Response<?> response = new Response<>(Operations.DELETE_SKILL, Statuses.SUCCESS);
                            responseMessage(response);
                            return;
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e);
                    Response<?> response = new Response<>(Operations.DELETE_SKILL, Statuses.ERROR);
                    responseMessage(response);
                }
            }
            case INCLUDE_JOB -> {
                IncludeSkillRequest includeSkillRequest = receivedRequest.withDataClass(IncludeSkillRequest.class).data();
                String token = receivedRequest.token();
                try {
                    verifier.verify(token);
                } catch (JWTVerificationException e) {
                    Response<?> response = new Response<>(Operations.INCLUDE_SKILL, Statuses.INVALID_TOKEN);
                    responseMessage(response);
                }
                try {
                    Map<String, Claim> decoded = JWT.decode(token).getClaims();
                    int id = decoded.get("id").asInt();
                    Recruiter recruiter = db.selectByPK(Recruiter.class, id);
                    Skill skill;
                    try {
                        skill = db.getOneByQuery("SELECT s FROM Skill s WHERE s.skill = '" + includeSkillRequest.skill() + "'", Skill.class);
                    } catch (NoResultException e) {
                        Response<?> response = new Response<>(Operations.INCLUDE_JOB, Statuses.SKILL_NOT_FOUND);
                        responseMessage(response);
                        return;
                    }
                    try {
                        List<Job> recruiterJobs = recruiter.getJobs();
                        for (Job jobs : recruiterJobs) {
                            if (jobs.getSkill().getId() == skill.getId()) {
                                Response<?> response = new Response<>(Operations.INCLUDE_JOB, Statuses.SKILL_EXISTS);
                                responseMessage(response);
                                return;
                            }
                        }
                    } catch (NoResultException e) {
                        // do nothing
                    }
                    Job job = new Job();
                    job.setRecruiter(recruiter);
                    job.setSkill(skill);
                    job.setExperience(Integer.parseInt(includeSkillRequest.experience()));
                    recruiter.getJobs().add(job);
                    db.update(recruiter);
                    Response<?> response = new Response<>(Operations.INCLUDE_SKILL, Statuses.SUCCESS);
                    responseMessage(response);
                } catch (Exception e) {
                    Response<?> response = new Response<>(Operations.INCLUDE_SKILL, Statuses.ERROR);
                    responseMessage(response);
                }
            }
            case LOOKUP_JOBSET -> {
                String token = receivedRequest.token();
                try {
                    verifier.verify(token);
                } catch (JWTVerificationException e) {
                    Response<?> response = new Response<>(Operations.LOOKUP_JOBSET, Statuses.INVALID_TOKEN);
                    responseMessage(response);
                }
                try {
                    LookUpSkillRequest skillRequest = receivedRequest.withDataClass(LookUpSkillRequest.class).data();
                    Candidate candidate = db.getOneByQuery("SELECT c FROM Candidate c WHERE c.id = " + JWT.decode(token).getClaims().get("id").asInt(), Candidate.class);

                    List<Experience> experiences = candidate.getExperiences();
                    List<ExperienceToResponse> skillset = new ArrayList<>();
                    for (Experience experience : experiences) {
                        skillset.add(new ExperienceToResponse(experience.getSkill().getSkill(), experience.getYears().toString()));
                    }
                    SkillSetResponse skillSetResponse = new SkillSetResponse(skillset.size(), skillset);
                    Response<SkillSetResponse> response = new Response<>(Operations.LOOKUP_SKILL, Statuses.SUCCESS, skillSetResponse);
                    responseMessage(response);
                } catch (Exception e) {
                    Response<?> response = new Response<>(Operations.LOOKUP_SKILL, Statuses.ERROR);
                    responseMessage(response);
                }
            }
        }
    }
}
