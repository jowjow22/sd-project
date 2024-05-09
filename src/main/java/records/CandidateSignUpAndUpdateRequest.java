package records;

import models.Candidate;

public record CandidateSignUpAndUpdateRequest(String name, String email, String password) {
    public CandidateSignUpAndUpdateRequest(Candidate candidate) {
        this(candidate.getName(), candidate.getEmail(), candidate.getPassword());
    }
}
