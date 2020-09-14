package lec.spring.studygroupclone.Events;

import lec.spring.studygroupclone.Models.Event;
import lec.spring.studygroupclone.Models.Study;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EventUpdated {
    private final Event event;
    private final String message;
}
