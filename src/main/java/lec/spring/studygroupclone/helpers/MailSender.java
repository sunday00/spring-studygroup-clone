package lec.spring.studygroupclone.helpers;

import lec.spring.studygroupclone.Models.EmailInfo;

public interface MailSender {
    void send(EmailInfo emailInfo);
}
