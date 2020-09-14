package lec.spring.studygroupclone.Events;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Models.Event;
import lec.spring.studygroupclone.Models.Study;
import lec.spring.studygroupclone.Repositories.AccountRepository;
import lec.spring.studygroupclone.Repositories.StudyRepository;
import lec.spring.studygroupclone.Services.NotificationService;
import lec.spring.studygroupclone.helpers.account.SendEmail;
import lec.spring.studygroupclone.helpers.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@Async
@Component
@RequiredArgsConstructor
public class EventListenerBase {

    private final NotificationService notificationService;

    private final SendEmail sendEmail;

    protected void sendNotification(Account account, Study study, NotificationType notificationType, String msg){
        if( account.isStudyCreatedAlarm() || account.isStudyUpdateAlarm()){

            notificationService.create(account, study, notificationType, msg);

            if( account.isEmailAlarm() && account.isEmailVerified()){
                sendEmail.sendStudyAlarm(account, study, notificationType, msg);
            }
        }
    }

    protected void sendNotification(Account account, Study study, Event event, NotificationType notificationType, String msg){
        if( account.isStudyCreatedAlarm() || account.isStudyJoinAllowAlarm()){

            notificationService.create(account, study, event, notificationType, msg);

            if( account.isEmailAlarm() && account.isEmailVerified()){
                sendEmail.sendStudyAlarm(account, study, event, notificationType, msg);
            }
        }
    }


}
