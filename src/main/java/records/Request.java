package records;


import enums.Operations;
import lombok.Getter;

public record Request<T>(@Getter Operations operation, @Getter T data) { }
