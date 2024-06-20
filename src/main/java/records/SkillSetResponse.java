package records;

import java.util.List;

public record SkillSetResponse(String skillset_size, List<ExperienceToResponse> skillset) {
}
