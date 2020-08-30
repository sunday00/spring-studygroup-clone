package lec.spring.studygroupclone.Services;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Models.Enrollment;
import lec.spring.studygroupclone.Models.Event;
import lec.spring.studygroupclone.Models.Study;
import lec.spring.studygroupclone.Repositories.EnrollmentRepository;
import lec.spring.studygroupclone.Repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EnrollmentRepository enrollmentRepository;

    public void getAllEvents() {
        eventRepository.findAll();
    }

    public List<Event> getAllEventsByStudy(Study study) {
        return eventRepository.findAllByStudy(study);
    }


    public Event create(Event event, Study study, Account account) {
        event.setAuthor(account);
        event.setStudy(study);
        event.setCreatedAt(LocalDateTime.now());
        return eventRepository.save(event);
    }

    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(NullPointerException::new);
//        return eventRepository.getOne(eventId);
    }

    public HashMap<String, Boolean> getCanAccountEnroll(Account account, Event event) {
        HashMap<String, Boolean> canAccountEnroll = new HashMap<>();
        Enrollment enrollment = enrollmentRepository.findByAccount(account);
        boolean isEnrolled = enrollment != null;
        boolean isEnrollClosed = isEnrollClosed(event);
        boolean isAttended = isEnrolled && enrollment.isAttended();

        canAccountEnroll.put("isEnrolled", isEnrolled);
        canAccountEnroll.put("isEnrollClosed", isEnrollClosed);
        canAccountEnroll.put("isEnrollable", !isEnrollClosed && !isEnrolled);
        canAccountEnroll.put("isCancelable", !isEnrollClosed && isEnrolled);
        canAccountEnroll.put("isAttended", isAttended);
        return canAccountEnroll;
    }

    private boolean isEnrollClosed(Event event) {
        return event.getEndEnrollmentAt().isBefore(LocalDateTime.now());
    }
}
