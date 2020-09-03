package lec.spring.studygroupclone.Models;

import lec.spring.studygroupclone.helpers.ConsoleLog;
import lec.spring.studygroupclone.helpers.event.EventType;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Lob @Basic(fetch = FetchType.EAGER)
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime endEnrollmentAt;

    public boolean isClosed(){
        return this.endEnrollmentAt.isBefore(LocalDateTime.now());
    }

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    @Column(nullable = false)
    private Integer limitEnrollment;

    @OneToMany(mappedBy = "event")
    private List<Enrollment> enrollments;

    public void addEnrollment(Enrollment enrollment){
        this.enrollments.add(enrollment);
        enrollment.setEvent(this);
    }

    public void removeEnrollment(Enrollment enrollment){
        this.enrollments.remove(enrollment);
    }

    public Enrollment getFirstWaiting(){
        for(Enrollment e : this.enrollments){
            if (!e.isAccepted()) return e;
        }
        return null;
    }

    public List<Enrollment> getWaitingByLength(int remainEnroll) {
        List<Enrollment> enrollmentsList = new ArrayList<>();
        for (Enrollment e : this.enrollments) {
            if (!e.isAccepted()) {
                enrollmentsList.add(e);
            }
            if (enrollmentsList.size() == remainEnroll) return enrollmentsList;
        }
        return enrollmentsList;
    }

    public List<Enrollment> getWaitingByLength() {
        return this.getWaitingByLength(this.remainEnroll());
    }

    public int remainEnroll(){
        return (int) (this.limitEnrollment - this.enrollments.stream().filter(Enrollment::isAccepted).count());
    }

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
