package lec.spring.studygroupclone.helpers.account;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Models.EmailInfo;
import lec.spring.studygroupclone.Repositories.AccountRepository;
import lec.spring.studygroupclone.helpers.MailSender;
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
}
