package records;

import enums.Available;

public record SetJobAvailableRequest(String id, Available available) {
}
