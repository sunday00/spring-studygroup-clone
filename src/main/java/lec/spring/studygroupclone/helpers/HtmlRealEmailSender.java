package lec.spring.studygroupclone.helpers;

import lec.spring.studygroupclone.Models.EmailInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Slf4j
@Profile({"dev"})
@RequiredArgsConstructor
@Component
public class HtmlRealEmailSender implements MailSender{
    private final JavaMailSender javaMailSender;

    @Override
    public void send(EmailInfo emailInfo) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, String.valueOf(StandardCharsets.UTF_8));
            helper.setTo(emailInfo.getTo());
            helper.setSubject(emailInfo.getSubject());
            helper.setText(emailInfo.getMessage(), false);
            javaMailSender.send(mimeMessage);
            log.info("Sent email : {}", emailInfo.getMessage());
        } catch (MessagingException e) {
            System.out.println("==========================");
            log.error("failed to send email", e);
            System.out.println("==========================");
        }
    }
}
