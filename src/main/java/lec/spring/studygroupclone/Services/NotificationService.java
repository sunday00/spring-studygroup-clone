package lec.spring.studygroupclone.Services;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Models.Event;
import lec.spring.studygroupclone.Models.Notification;
import lec.spring.studygroupclone.Models.Study;
import lec.spring.studygroupclone.Repositories.NotificationRepository;
import lec.spring.studygroupclone.helpers.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void create(Account account, Study study, NotificationType notificationType, String msg) {
        setNotification(account, study, notificationType, msg);
    }

    public void create(Account account, Study study, Event event, NotificationType notificationType, String msg) {
        setNotification(account, study, event, notificationType, msg);
    }

    public void setNotification(Account account, Study study, NotificationType notificationType, String msg){
        Notification notification = new Notification();

        switch (notificationType){
            case STUDY_CREATED:
                notification.setTitle(study.getId() + "/" + study.getTitle() + " is created and open.");
                notification.setLink("/study/read/show/" + study.getPath());
                notification.setMessage(study.getIntroduce());
                break;
            case UPDATED_STUDY:
                notification.setTitle(study.getId() + "/" + study.getTitle());
                notification.setLink("/study/read/show/" + study.getPath());
                notification.setMessage(study.getIntroduce() + " : " + msg);
                break;
        }

        notification.setChecked(false);
        notification.setAccount(account);
        notification.setCreated_at(LocalDateTime.now());
        notification.setNotificationType(notificationType);

        notificationRepository.save(notification);
    }

    public void setNotification(Account account, Study study, Event event, NotificationType notificationType, String msg){
        Notification notification = new Notification();

        switch (notificationType){
            case EVENT_CREATED:
                notification.setTitle(study.getTitle() + " / " + event.getTitle() + " has been created.");
                notification.setLink("/study/"+ study.getPath() +"/event/show/" + event.getId());
                notification.setMessage("end of enroll : " + event.getEndEnrollmentAt().toString().replace("T", " "));
                break;
            case UPDATED_EVENT:
                notification.setTitle(study.getTitle() + " / " + event.getTitle() + " has been updated.");
                notification.setLink("/study/"+ study.getPath() +"/event/show/" + event.getId());
                notification.setMessage(study.getTitle() + " / " + event.getTitle() + " : " + msg);
                break;
            case DELETE_EVENT:
                notification.setTitle(study.getTitle() + " / " + event.getTitle() + " has been deleted.");
                notification.setLink("/study/" + study.getPath() + "/events");
                notification.setMessage("Bye~");
                break;
        }

        notification.setChecked(false);
        notification.setAccount(account);
        notification.setCreated_at(LocalDateTime.now());
        notification.setNotificationType(notificationType);

        notificationRepository.save(notification);
    }

    public Long getAlarmCount(Account account, boolean read) {
        return notificationRepository.countByAccountAndChecked(account, read);
    }

    public List<Notification> getUnread(Account account) {
        return notificationRepository.findByAccountAndChecked(account, false);
    }

    public HashMap<String, List<Notification>> getUnreadSets(Account account) {
        List<Notification> list = this.getUnread(account);
        HashMap<String, List<Notification>> unreadSets = new HashMap<>();

        List<Notification> studyCreatedLists = new ArrayList<>();
        List<Notification> studyUpdatedLists = new ArrayList<>();
        List<Notification> eventCreatedLists = new ArrayList<>();
        List<Notification> eventUpdatedLists = new ArrayList<>();

        for(Notification l : list){
            switch (l.getNotificationType()){
                case STUDY_CREATED:
                    studyCreatedLists.add(l);
                    break;
                case UPDATED_STUDY:
                    studyUpdatedLists.add(l);
                    break;
                case EVENT_CREATED:
                    eventCreatedLists.add(l);
                    break;
                case UPDATED_EVENT:
                case DELETE_EVENT:
                    eventUpdatedLists.add(l);
                    break;
            }
        }

        unreadSets.put("all", list);
        unreadSets.put("studyCreatedLists", studyCreatedLists);
        unreadSets.put("studyUpdatedLists", studyUpdatedLists);
        unreadSets.put("eventCreatedLists", eventCreatedLists);
        unreadSets.put("eventUpdatedLists", eventUpdatedLists);

        return unreadSets;
    }

    public void notificationRead(Account account, Long id) {
        Notification notification = notificationRepository.findByAccountAndId(account, id);
        if( account == null || notification == null ) throw new NullPointerException("no notification or not your notification");
        notification.setChecked(true);
    }
}
