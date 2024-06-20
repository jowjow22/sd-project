package models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "recruiters")
public class Recruiter {
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
    @Column(name = "industry", length = 255, nullable = false)
    private String industry;
    @Column(name = "description", length = 255, nullable = false)
    private String description;

    @OneToMany(mappedBy = "recruiter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Job> jobs = new ArrayList<>();

    public Recruiter() {
    }
}
