package lec.spring.studygroupclone.Services;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Models.Enrollment;
import lec.spring.studygroupclone.Models.Event;
import lec.spring.studygroupclone.Repositories.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;


    public void leave(Event event, Account account) {
        enrollmentRepository.deleteByEventAndAccount(event, account);
    }

    public Enrollment apply(Event event, Account account) {
        Enrollment enrollment = new Enrollment();
        enrollment.setEvent(event);
        enrollment.setAccount(account);
        enrollment.setAttended(false);

        //TODO :: logic with accepted

        return enrollmentRepository.save(enrollment);
    }
}
