package lec.spring.studygroupclone.Events;

import lec.spring.studygroupclone.Models.Study;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class StudyCreated {

    private Study study;

    public StudyCreated(Study study) {
       this.study = study;
    }
}
