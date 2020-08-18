package lec.spring.studygroupclone.Models;

import lombok.Data;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@Slf4j
public class EmailInfo {
    private String to;

    private String subject;

    private String message;
}
