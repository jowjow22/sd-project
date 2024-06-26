package models;

import enums.Available;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "recruiter_id", nullable = false)
    private Recruiter recruiter;

    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(name = "experience", nullable = false)
    private Integer experience;

    @Column(name = "available", length = 255, nullable = false)
    private Available available;

    @Column(name = "searchable", length = 255, nullable = false)
    private Available searchable;
}
