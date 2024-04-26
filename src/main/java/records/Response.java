package records;

import enums.Operations;
import enums.Statuses;
import lombok.Getter;

public record Response<T>(@Getter Operations operation, @Getter Statuses response, @Getter T data) { }