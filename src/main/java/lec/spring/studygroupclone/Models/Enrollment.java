package lec.spring.studygroupclone.Models;

import lec.spring.studygroupclone.helpers.event.EventType;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@EqualsAndHashCode(of = "id")
public class Enrollment {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Event event;

    @ManyToOne
    private Account account;

    private LocalDateTime enrolledAt;

    private boolean accepted;

    private boolean attended;

    public boolean isAcceptable(Event event){
        return !this.isEnrollClosed(event) && !this.accepted && event.getEventType() == EventType.CONFIRMATIVE
                && event.getLimitEnrollment() > event.getEnrollmentsByAccepted(true).size();
    }

    public boolean isDeniable(Event event){
        return !this.isEnrollClosed(event) && this.accepted && event.getEventType() == EventType.CONFIRMATIVE
                && !this.isAttended();
    }

    public boolean isManageable(Event event){
        return this.isAcceptable(event) || this.isDeniable(event);
    }

    private boolean isEnrollClosed(Event event) {
        LocalDateTime closeEnrollTime = event.getEndEnrollmentAt();
        return closeEnrollTime.isBefore(LocalDateTime.now());
    }
}
