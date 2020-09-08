package lec.spring.studygroupclone.Events;

import lec.spring.studygroupclone.Models.Study;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@Async
@Component
public class StudyListener {
    @EventListener
    public void handleStudyCreated(StudyCreated studyCreated){
        Study study = studyCreated.getStudy();
        log.info(study.getTitle() + " is created.");

//        throw new RuntimeException("Test exception ===================");
    }
}
