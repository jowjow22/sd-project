package records;

import models.Recruiter;

public record RecruiterResponse(String description, String email, String name, String password, String industry) {
    public RecruiterResponse(Recruiter recruiter) {
        this(recruiter.getDescription(), recruiter.getEmail(), recruiter.getName(), recruiter.getPassword(), recruiter.getIndustry());
    }
}
