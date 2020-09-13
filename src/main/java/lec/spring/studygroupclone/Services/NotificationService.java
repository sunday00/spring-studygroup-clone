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

    public void setNotification(Account account, Study study, Event event, NotificationType notificationType){
        Notification notification = new Notification();

        switch (notificationType){
            case ALLOW_JOINING:
                notification.setTitle("You are allowing to join " + event.getId() + "/" + event.getTitle() + ".");
                notification.setLink("/study/"+ study.getPath() +"/event/show/" + event.getId());
                notification.setMessage(event.getDescription());
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
//        List<Notification> studyCreatedNotifications = new ArrayList<>();

        for(Notification l : list){
            switch (l.getNotificationType()){
                case STUDY_CREATED:
                    studyCreatedLists.add(l);
                    break;
                case UPDATED_STUDY:
                    studyUpdatedLists.add(l);
                    break;
            }
        }

        unreadSets.put("all", list);
        unreadSets.put("studyCreatedLists", studyCreatedLists);
        unreadSets.put("studyUpdatedLists", studyUpdatedLists);

        return unreadSets;
    }
}
