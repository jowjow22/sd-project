package records;

import java.util.List;

public record SearchCandidateRequest(List<String> skill, String experience, String filter) {
}
