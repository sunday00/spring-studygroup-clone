package lec.spring.studygroupclone.helpers.event;

import lec.spring.studygroupclone.Repositories.EventRepository;
import lec.spring.studygroupclone.dataMappers.EventSetting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EventSettingValidator implements Validator {

    private final EventRepository eventRepository;

    @Override
    public boolean supports(Class<?> clazz) {
//                return clazz.isAssignableFrom(EventSetting.class);
                return EventSetting.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EventSetting eventSetting = (EventSetting) target;

        if( isEnrollmentEndTimeValid(eventSetting) ){
            errors.rejectValue("endEnrollmentAt", "wrong.dateTime", "Do not select past.");
        }

        if( isStartTimeValid(eventSetting) ){
            errors.rejectValue("startAt", "wrong.dateTime", "Do not select more future then end of event.");
        }

        if( isEndTimeValid(eventSetting) ){
            errors.rejectValue("endAt", "wrong.dateTime", "Do not select more previous then start of event.");
        }


    }

    private boolean isEnrollmentEndTimeValid(EventSetting eventSetting){
        return eventSetting.getEndEnrollmentAt().isBefore(LocalDateTime.now());
    }

    private boolean isStartTimeValid(EventSetting eventSetting){
        return eventSetting.getEndAt().isBefore(eventSetting.getStartAt())
                || eventSetting.getStartAt().isBefore(LocalDateTime.now());
    }

    private boolean isEndTimeValid(EventSetting eventSetting){
        return eventSetting.getEndAt().isBefore(eventSetting.getStartAt())
                || eventSetting.getEndAt().isBefore(LocalDateTime.now());
    }
}
