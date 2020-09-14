package lec.spring.studygroupclone.Events;

import lec.spring.studygroupclone.Models.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EventDeleted {
    private final Event event;
    private final String message;
}
