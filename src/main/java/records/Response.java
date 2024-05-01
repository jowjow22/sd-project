package records;

import enums.Operations;
import enums.Statuses;

public record Response<T>(Operations operation, Statuses status, T data) {
}
