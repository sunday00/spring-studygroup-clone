package lec.spring.studygroupclone.Events;

import lec.spring.studygroupclone.Models.Study;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StudyUpdated{

    private final Study study;

    private final String message;
}
