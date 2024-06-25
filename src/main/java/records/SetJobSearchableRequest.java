package records;

import enums.Available;

public record SetJobSearchableRequest(String id, Available searchable) {
}
