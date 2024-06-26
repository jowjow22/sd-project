package records;

import java.util.List;

public record SearchCandidatesResponse(String profile_size, List<CandidateToSearchResponse> profile) {
}
