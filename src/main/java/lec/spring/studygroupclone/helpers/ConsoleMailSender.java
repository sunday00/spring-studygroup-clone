package lec.spring.studygroupclone.helpers;

import lec.spring.studygroupclone.Models.EmailInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile({"local"})
@Component
public class ConsoleMailSender implements MailSender {
    @Override
    public void send(EmailInfo emailInfo) {
        log.info("Sent email : {}", emailInfo.getMessage());
    }
}
