package lec.spring.studygroupclone.Models;

import lec.spring.studygroupclone.helpers.event.EventType;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor
public class Event {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Study study;

    @ManyToOne
    private Account author;

    @Column(nullable = false)
    @Pattern(regexp = "^[a-zA-Z0-9-_]{3,25}$", message = "It should be 3-25 characters using alphabet, digit, dash(-) or underscore(_) only.")
    private String title;

    @Lob
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime endEnrollmentAt;

    @Column(nullable = false)
    private LocalDateTime start;

    @Column(nullable = false)
    private LocalDateTime end;

    @Column(nullable = false)
    private Integer limitEnrollment;

    @OneToMany(mappedBy = "event")
    private List<Enrollment> enrollments;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

}
