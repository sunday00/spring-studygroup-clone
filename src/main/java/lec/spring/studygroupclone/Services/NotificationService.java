package lec.spring.studygroupclone.Services;

import lec.spring.studygroupclone.Models.Account;
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
        Notification notification = new Notification();

        notification.setTitle(study.getId() + "/" + study.getTitle());
        notification.setLink("/study/read/show/" + study.getPath());
        notification.setMessage(study.getIntroduce());
        notification.setChecked(false);
        notification.setAccount(account);
        notification.setCreated_at(LocalDateTime.now());
        notification.setNotificationType(NotificationType.STUDY_CREATED);

        notificationRepository.save(notification);
    }

    public void setNotification(){

    }
}
