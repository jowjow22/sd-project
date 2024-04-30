package records;

import lombok.Getter;

public record CandidateLoginRequest(@Getter String email, @Getter String password) {
}
