package models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Entity
@Table(name = "candidates")
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String email;
    private String name;
    private String password;

    public Candidate() {
    }
}
