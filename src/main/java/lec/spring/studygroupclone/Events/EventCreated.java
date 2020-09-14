package lec.spring.studygroupclone.Events;

import lec.spring.studygroupclone.Models.Event;
import lec.spring.studygroupclone.Models.Study;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;

@Getter
@RequiredArgsConstructor
public class EventCreated{

    private final Study study;
    private final Event event;

}
