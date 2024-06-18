package records;

import models.Candidate;

public record CandidateResponse (String email, String name, String password){
    public CandidateResponse(Candidate candidate) {
        this(candidate.getEmail(), candidate.getName(), candidate.getPassword());
    }
}
