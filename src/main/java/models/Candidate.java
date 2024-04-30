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
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "email", length = 255, nullable = false, unique = true)
    private String email;
    @Column(name = "name", length = 255, nullable = false)
    private String name;
    @Column(name = "password", length = 255, nullable = false)
    private String password;

    public Candidate() {
    }
}
