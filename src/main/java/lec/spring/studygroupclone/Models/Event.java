package lec.spring.studygroupclone.Models;

import lec.spring.studygroupclone.helpers.event.EventType;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.List;

@NamedEntityGraph(name = "Event.withEnrollments", attributeNodes = {
    @NamedAttributeNode("enrollments")
})
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
    @Length(min = 3, max = 50, message = "It should be 3-50 characters.")
    private String title;

    @Lob
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime endEnrollmentAt;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    @Column(nullable = false)
    private Integer limitEnrollment;

    @OneToMany(mappedBy = "event")
    private List<Enrollment> enrollments;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    public boolean isEnrolled(Account account){
        for ( Enrollment e : this.enrollments ){
            if( e.getAccount().equals(account) ){
                return true;
            }
        }

        return false;
    }

}
