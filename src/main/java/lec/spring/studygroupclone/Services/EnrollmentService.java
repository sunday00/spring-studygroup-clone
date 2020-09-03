package lec.spring.studygroupclone.Services;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Models.Enrollment;
import lec.spring.studygroupclone.Models.Event;
import lec.spring.studygroupclone.Repositories.EnrollmentRepository;
import lec.spring.studygroupclone.helpers.ConsoleLog;
import lec.spring.studygroupclone.helpers.event.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;


    public void leave(Event event, Account account) {
        Enrollment enrollment = enrollmentRepository.findByAccountAndEvent(account, event);
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
}
