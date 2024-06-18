package records;

import models.Skill;

import java.util.List;

public record GetAvailableSkillsResponse(List<Skill> skills) {
}
