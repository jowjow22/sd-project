package records;

import lombok.Getter;

public record RecruiterLoginRequest(@Getter String email, @Getter String password) {
}
