package lec.spring.studygroupclone.Services;

import lec.spring.studygroupclone.Events.EventUpdated;
import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Models.Enrollment;
import lec.spring.studygroupclone.Models.Event;
import lec.spring.studygroupclone.Repositories.EnrollmentRepository;
import lec.spring.studygroupclone.helpers.ConsoleLog;
import lec.spring.studygroupclone.helpers.event.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void leave(Event event, Account account) {
        Enrollment enrollment = enrollmentRepository.findByAccountAndEvent(account, event);
        if(enrollment.isAttended()){
            throw new IllegalArgumentException("Already attended enrollment can't be deleted");
        }

        event.removeEnrollment(enrollment);
        enrollmentRepository.deleteByEventAndAccount(event, account);
        if( event.remainEnroll() > 0 && event.getEventType() == EventType.FCFS ){
            Enrollment waitingEnroll = event.getFirstWaiting();
            if(waitingEnroll != null ) waitingEnroll.setAccepted(true);
        }
    }

    public Enrollment apply(Event event, Account account) {
        Enrollment enrollment = new Enrollment();
        enrollment.setAccount(account);
        enrollment.setAttended(false);

        enrollment.setAccepted(event.remainEnroll() > 0 && event.getEventType() == EventType.FCFS);

        event.addEnrollment(enrollment);
        enrollment.setEnrolledAt(LocalDateTime.now());

        return enrollmentRepository.save(enrollment);
    }

    public void acceptEnroll(Long id, Event event) {
        enrollmentRepository.getOne(id).setAccepted(true);
        applicationEventPublisher.publishEvent(new EventUpdated(event, "You allow the joining event."));
    }

    public void rejectEnroll(Long id, Event event) {
        enrollmentRepository.getOne(id).setAccepted(false);
        applicationEventPublisher.publishEvent(new EventUpdated(event, "You reject the joining event."));
    }

    public void attend(Long id) {
        enrollmentRepository.getOne(id).setAttended(true);
    }

    public void cancelAttend(Long id) {
        enrollmentRepository.getOne(id).setAttended(false);
    }
}
