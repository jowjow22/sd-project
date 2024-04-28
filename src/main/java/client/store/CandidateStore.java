package client.store;

import lombok.Getter;
import lombok.Setter;
import models.Candidate;

@Getter
@Setter
public class CandidateStore {
    private static CandidateStore instance = null;
    private String token;
    private Candidate candidate;


    private CandidateStore() {
    }

    public static CandidateStore getInstance() {
        if (instance == null) {
            instance = new CandidateStore();
        }
        return instance;
    }

    public void setCandidate(Candidate candidate, String token) {
        this.candidate = candidate;
        this.token = token;
    }

}
