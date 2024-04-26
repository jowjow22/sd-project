package records;

import enums.Operations;
import lombok.Getter;

public record Response<T>(@Getter Operations operation, @Getter T data) { }