package client.store;

import lombok.Getter;
import lombok.Setter;
import models.Recruiter;

@Getter
@Setter
public class RecruiterStore {
    private static RecruiterStore instance = null;
    private String token;
    private Recruiter recruiter;

    private RecruiterStore() {
    }

    public static RecruiterStore getInstance() {
        if (instance == null) {
            instance = new RecruiterStore();
        }
        return instance;
    }
}
