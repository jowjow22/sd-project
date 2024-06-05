package records;

import models.Recruiter;

public record RecruiterSignUpAndUpdateRequest(String email, String password, String name, String industry, String description) {
    public RecruiterSignUpAndUpdateRequest(Recruiter recruiter) {
        this(recruiter.getEmail(), recruiter.getPassword(), recruiter.getName(), recruiter.getIndustry(), recruiter.getDescription());
    }
}
