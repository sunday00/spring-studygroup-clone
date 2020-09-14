package lec.spring.studygroupclone.Events;

import lec.spring.studygroupclone.Models.*;
import lec.spring.studygroupclone.Repositories.*;
import lec.spring.studygroupclone.Services.EventService;
import lec.spring.studygroupclone.Services.NotificationService;
import lec.spring.studygroupclone.helpers.ConsoleLog;
import lec.spring.studygroupclone.helpers.account.SendEmail;
import lec.spring.studygroupclone.helpers.notification.NotificationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Transactional
@Async
@Component
public class EventListener extends EventListenerBase{

    private final EnrollmentRepository enrollmentRepository;
    private final StudyRepository studyRepository;

    public EventListener(NotificationService notificationService,
                         SendEmail sendEmail,
                         EnrollmentRepository enrollmentRepository,
                         StudyRepository studyRepository) {
        super(notificationService, sendEmail);
        this.enrollmentRepository = enrollmentRepository;
        this.studyRepository = studyRepository;
    }

    @org.springframework.context.event.EventListener
    public void handleEventCreated(EventCreated eventCreated){
        Study study = studyRepository.findById(eventCreated.getStudy().getId()).orElseThrow(NullPointerException::new);
        Set<Account> accounts = study.getMembers();
        accounts.addAll(study.getManagers());

        accounts.forEach(a -> {
            this.sendNotification(a, study, eventCreated.getEvent(), NotificationType.EVENT_CREATED, "Event Created.");
        });
    }

    @org.springframework.context.event.EventListener
    public void handleEventUpdated(EventUpdated eventUpdated){
        Study study = studyRepository.findById(eventUpdated.getEvent().getStudy().getId()).orElseThrow(NullPointerException::new);

        List<Enrollment> enrollments = enrollmentRepository.findAllByEvent(eventUpdated.getEvent());

        enrollments.forEach(e -> {
            this.sendNotification(e.getAccount(), study, eventUpdated.getEvent(), NotificationType.UPDATED_EVENT, eventUpdated.getMessage());
        });
    }

    @org.springframework.context.event.EventListener
    public void handleEventDeleted(EventDeleted eventDeleted){
        Study study = studyRepository.findById(eventDeleted.getEvent().getStudy().getId()).orElseThrow(NullPointerException::new);

        List<Enrollment> enrollments = enrollmentRepository.findAllByEvent(eventDeleted.getEvent());

        enrollments.forEach(e -> {
            this.sendNotification(e.getAccount(), study, eventDeleted.getEvent(), NotificationType.DELETE_EVENT, eventDeleted.getMessage());
        });
    }

}
