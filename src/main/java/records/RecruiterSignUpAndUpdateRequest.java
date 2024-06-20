package records;

import models.Recruiter;

public record RecruiterSignUpAndUpdateRequest( String name, String email, String password, String industry, String description) {
    public RecruiterSignUpAndUpdateRequest(Recruiter recruiter) {
        this(recruiter.getEmail(), recruiter.getPassword(), recruiter.getName(), recruiter.getIndustry(), recruiter.getDescription());
    }
}
