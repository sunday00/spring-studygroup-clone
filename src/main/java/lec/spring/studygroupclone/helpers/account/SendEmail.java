package lec.spring.studygroupclone.helpers.account;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Models.EmailInfo;
import lec.spring.studygroupclone.Models.Event;
import lec.spring.studygroupclone.Models.Study;
import lec.spring.studygroupclone.Repositories.AccountRepository;
import lec.spring.studygroupclone.helpers.MailSender;
import lec.spring.studygroupclone.helpers.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendEmail {

    private final MailSender mailSender;
    private final AccountRepository accountRepository;
    private final TemplateEngine templateEngine;

    private final String host = InetAddress.getLoopbackAddress().getHostName().endsWith("localhost") ? "http://localhost:8080" : "http://"+InetAddress.getLoopbackAddress().getHostName();

    public void sendSignupConfirmEmail(Account member) {
        String host = InetAddress.getLoopbackAddress().getHostName().endsWith("localhost") ? "http://localhost:8080" : "http://"+InetAddress.getLoopbackAddress().getHostName();

        Context context = new Context();
        context.setVariable("nickname", member.getNickname());

        context.setVariable("host", host);

        context.setVariable("link", "/check-email-token?" + "token=" + member.getEmailCheckToken() + "&" + "email=" + member.getEmail());

        String message = templateEngine.process("mail/verify", context);

        EmailInfo emailInfo = EmailInfo.builder()
                .to(member.getEmail())
                .subject("STUDY GROUP SITE JOINED")
                .message(message)
                .build();
        mailSender.send(emailInfo);
    }

    public boolean resendVerificationToken(Account account, String active) {
        Account member = accountRepository.findByEmail(account.getEmail());
        if (member.canSendEmailCheckToken(active)) {
            member.generateEmailCheckToken();
            this.sendSignupConfirmEmail(member);
            accountRepository.save(member);
            return true;
        }
        return false;
    }

    public Account checkEmailToken(String token, String email) {
        Account account = accountRepository.findByEmail(email);
        if (account == null || !account.getEmailCheckToken().equals(token)) return null;

        account.setEmailVerified(true);
        account.setJoinedAt(LocalDateTime.now());
        accountRepository.save(account);
        return account;
    }

    public void sendStudyAlarm(Account member, Study study, NotificationType type, String msg) {
        Context context = new Context();
        context.setVariable("nickname", member.getNickname());

        context.setVariable("host", host);

        context.setVariable("study", study);

        String message;
        switch (type){
            case STUDY_CREATED:
                context.setVariable("link", "/study/read/show/" + study.getPath());
                context.setVariable("notificationOffLink", "/profile/noti");
                context.setVariable("title", study.getTitle());
                context.setVariable("state", "created and open.");
                message = templateEngine.process("mail/studyCreated", context);
                break;
            case UPDATED_STUDY:
                context.setVariable("link", "/study/read/show/" + study.getPath());
                context.setVariable("notificationOffLink", "/profile/noti");
                context.setVariable("title", study.getTitle());
                context.setVariable("state", msg + ".");
                message = templateEngine.process("mail/studyCreated", context);
                break;
            default:
                context.setVariable("link", "/study/");
                message = templateEngine.process("mail/", context);
                break;
        }

        EmailInfo emailInfo = EmailInfo.builder()
                .to(member.getEmail())
                .subject("STUDY GROUP SITE NOTIFICATION")
                .message(message)
                .build();
        mailSender.send(emailInfo);
    }

    public void sendStudyAlarm(Account member, Study study, Event event, NotificationType type, String msg) {
        Context context = new Context();
        context.setVariable("nickname", member.getNickname());

        context.setVariable("host", host);

        context.setVariable("study", study);

        context.setVariable("event", event);

        String message;
        switch (type){
            case EVENT_CREATED:
                context.setVariable("link", "/study/" + study.getPath() + "/event/" + event.getId());
                context.setVariable("notificationOffLink", "/profile/noti");
                context.setVariable("title", study.getTitle() + " / " + event.getTitle());
                context.setVariable("state", "Event created.");
                message = templateEngine.process("mail/studyCreated", context);
                break;
            case UPDATED_EVENT:
                context.setVariable("link", "/study/" + study.getPath() + "/event/" + event.getId());
                context.setVariable("notificationOffLink", "/profile/noti");
                context.setVariable("title", study.getTitle() + " / " + event.getTitle());
                context.setVariable("state", msg);
                message = templateEngine.process("mail/studyCreated", context);
                break;
            case DELETE_EVENT:
                context.setVariable("link", "/study/" + study.getPath() + "/events");
                context.setVariable("notificationOffLink", "/profile/noti");
                context.setVariable("title", study.getTitle() + " / " + event.getTitle());
                context.setVariable("state", msg);
                message = templateEngine.process("mail/studyCreated", context);
                break;
            default:
                context.setVariable("link", "/study/");
                message = templateEngine.process("mail/", context);
                break;
        }

        EmailInfo emailInfo = EmailInfo.builder()
                .to(member.getEmail())
                .subject("STUDY GROUP SITE NOTIFICATION")
                .message(message)
                .build();
        mailSender.send(emailInfo);
    }
}
