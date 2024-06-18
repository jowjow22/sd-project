package models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "skills")
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "skill", length = 255, nullable = false, unique = true)
    private String skill;

    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Experience> experiences = new ArrayList<>() {
    };

    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Job> jobs = new ArrayList<>() {
    };

    public Skill() {
    }

    public Skill(String skill) {
        this.skill = skill;
    }
}
