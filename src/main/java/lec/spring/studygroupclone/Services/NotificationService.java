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

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void create(Account account, Study study) {
        setNotification(account, study, NotificationType.STUDY_CREATED);
    }

    public void setNotification(Account account, Study study, NotificationType notificationType){
        Notification notification = new Notification();

        switch (notificationType){
            case ALLOW_JOINING:
                notification.setTitle(study.getId() + "/" + study.getTitle() + "");
                notification.setLink("/study/read/show/" + study.getPath());
                notification.setMessage(study.getIntroduce());
                break;
            case STUDY_CREATED:
                notification.setTitle(study.getId() + "/" + study.getTitle() + " is created and open.");
                notification.setLink("/study/read/show/" + study.getPath());
                notification.setMessage(study.getIntroduce());
                break;
            case UPDATED_STUDY:
                notification.setTitle(study.getId() + "/" + study.getTitle() + " is updated");
                notification.setLink("/study/read/show/" + study.getPath());
                notification.setMessage(study.getIntroduce());
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
}
