package records;

import models.Recruiter;

public record CompanyToResponse(String name, String industry, String description, String email) {
    public CompanyToResponse(Recruiter recruiter){
        this(recruiter.getName(), recruiter.getIndustry(), recruiter.getDescription(), recruiter.getEmail());
    }
}
