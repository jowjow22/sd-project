package records;

import java.util.List;

public record SkillSetResponse(Integer skillset_size, List<ExperienceToResponse> skillset) {
}
