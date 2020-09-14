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
public class StudyListener extends EventListenerBase {

    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;

    public StudyListener(NotificationService notificationService, SendEmail sendEmail, StudyRepository studyRepository, AccountRepository accountRepository) {
        super(notificationService, sendEmail);
        this.studyRepository = studyRepository;
        this.accountRepository = accountRepository;
    }

    @EventListener
    public void handleStudyCreated(StudyCreated studyCreated){
//        Study study = studyCreated.getStudy();
//        log.info(study.getTitle() + " is created.");
//        throw new RuntimeException("Test exception ===================");

        Study study = studyRepository.findWithTagsAndLocationsById( studyCreated.getStudy().getId() );
        Set<Tag> tags = study.getTags();
        Set<Location> locations = study.getLocations();

        Iterable<Account> accounts =  accountRepository.findAll(AccountPredicate.findByTagsAnsLocations(tags, locations));

        accounts.forEach(a -> {
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
}
