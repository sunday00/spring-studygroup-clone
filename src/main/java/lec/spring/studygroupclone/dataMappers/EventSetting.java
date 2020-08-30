package lec.spring.studygroupclone.dataMappers;

import lec.spring.studygroupclone.helpers.event.EventType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class EventSetting {

    @NotBlank
    @Length(min = 3, max = 50, message = "It should be 3-50 characters.")
    private String title;

    private String description;

//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endEnrollmentAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endAt;

    @Min(2)
    private Integer limitEnrollment;

    private EventType eventType = EventType.FCFS;
}
