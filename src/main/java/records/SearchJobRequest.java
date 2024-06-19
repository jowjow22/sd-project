package records;

import java.util.List;

public record SearchJobRequest(List<String> skill, String experience, String filter) {
}
