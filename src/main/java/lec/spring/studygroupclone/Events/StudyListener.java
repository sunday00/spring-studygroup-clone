package lec.spring.studygroupclone.Events;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Models.Location;
import lec.spring.studygroupclone.Models.Study;
import lec.spring.studygroupclone.Models.Tag;
import lec.spring.studygroupclone.Repositories.AccountPredicate;
import lec.spring.studygroupclone.Repositories.AccountRepository;
import lec.spring.studygroupclone.Repositories.StudyRepository;
import lec.spring.studygroupclone.Services.NotificationService;
import lec.spring.studygroupclone.helpers.account.SendEmail;
import lec.spring.studygroupclone.helpers.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Transactional
@Async
@Component
@RequiredArgsConstructor
public class StudyListener {

    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;
    private final NotificationService notificationService;

    private final SendEmail sendEmail;

    @EventListener
    public void handleStudyCreated(StudyCreated studyCreated){
//        Study study = studyCreated.getStudy();
//        log.info(study.getTitle() + " is created.");
//        throw new RuntimeException("Test exception ===================");

        Study study = studyRepository.findWithTagsAndLocationsById( studyCreated.getStudy().getId() );
        Set<Tag> tags = study.getTags();
        Set<Location> locations = study.getLocations();

        Iterable<Account> acccounts =  accountRepository.findAll(AccountPredicate.findByTagsAnsLocations(tags, locations));

        acccounts.forEach(a -> {
            this.sendNotification(a, study, NotificationType.STUDY_CREATED, null);
        });
    }

    @EventListener
    public void handleStudyUpdated(StudyUpdated studyUpdated){
        Study study = studyRepository.findStudyWithManagersAndMembersById(studyUpdated.getStudy().getId());
        Set<Account> accounts = new HashSet<>();

        accounts.addAll(study.getManagers());
        accounts.addAll(study.getMembers());

        accounts.forEach(a -> {
            this.sendNotification(a, study, NotificationType.UPDATED_STUDY, studyUpdated.getMessage());
        });
    }

    private void sendNotification(Account account, Study study, NotificationType notificationType, String msg){
        if( account.isStudyCreatedAlarm() || account.isStudyUpdateAlarm()){

            notificationService.create(account, study, notificationType, msg);

            if( account.isEmailAlarm() && account.isEmailVerified()){
                sendEmail.sendStudyAlarm(account, study, notificationType, msg);
            }
        }
    }
}
