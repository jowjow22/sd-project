package records;

import enums.Available;

public record IncludeJobRequest(String skill, String experience, Available available, Available searchable)  {
}
