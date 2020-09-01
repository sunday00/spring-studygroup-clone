package lec.spring.studygroupclone.Services;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Models.Enrollment;
import lec.spring.studygroupclone.Models.Event;
import lec.spring.studygroupclone.Models.Study;
import lec.spring.studygroupclone.Repositories.EnrollmentRepository;
import lec.spring.studygroupclone.Repositories.EventRepository;
import lec.spring.studygroupclone.dataMappers.EventSetting;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final ModelMapper modelMapper;

    private final EventRepository eventRepository;
    private final EnrollmentRepository enrollmentRepository;

    public void getAllEvents() {
        eventRepository.findAll();
    }

    public HashMap<String, List<Event>> getAllEventsByStudy(Study study) {
        HashMap<String, List<Event>> data = new HashMap<>();
        List<Event> lists = eventRepository.findAllByStudyOrderByStartAt(study);
        List<Event> comingEvents = new ArrayList<>();
        List<Event> pastEvents = new ArrayList<>();

        lists.forEach(list -> {
            if( list.getEndAt().isBefore(LocalDateTime.now()) ) pastEvents.add(list);
            else comingEvents.add(list);
        });

        data.put("all", lists);
        data.put("coming", comingEvents);
        data.put("past", pastEvents);

        return data;
    }

    public HashMap<String, Object> getComingEventsByStudy(Study study) {
        HashMap<String, Object> data = new HashMap<>();
        Integer cntPast = eventRepository.countByEndAtBeforeAndStudy(LocalDateTime.now(), study);
        List<Event> list = eventRepository.findAllWithEndAtAfterNowByStudy(LocalDateTime.now(), study);

        data.put("cntPast", cntPast);
        data.put("coming", list);

        return data;
    }

    public HashMap<String, Object> getPastEventsByStudy(Study study) {
        HashMap<String, Object> data = new HashMap<>();
        Integer cntComing = eventRepository.countByEndAtAfterAndStudy(LocalDateTime.now(), study);
        List<Event> list = eventRepository.findAllWithEndAtBeforeNowByStudy(LocalDateTime.now(), study);

        data.put("cntComing", cntComing);
        data.put("past", list);

        return data;
    }

    public Event create(Event event, Study study, Account account) {
        event.setAuthor(account);
        event.setStudy(study);
        event.setCreatedAt(LocalDateTime.now());
        return eventRepository.save(event);
    }

    public Event update(EventSetting eventSetting, Event event) {
        eventSetting.setEventType(event.getEventType());
        modelMapper.map(eventSetting, event);

        // TODO:: if the limitation is grow,
        //  modify some enrollments should be allowed
        //  when the type is FCFS.

        return event;
    }

    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(NullPointerException::new);
//        return eventRepository.getOne(eventId);
    }

    public HashMap<String, Boolean> getCanAccountEnroll(Account account, Event event) {
        HashMap<String, Boolean> canAccountEnroll = new HashMap<>();
        Enrollment enrollment = enrollmentRepository.findByAccountAndEvent(account, event);
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
