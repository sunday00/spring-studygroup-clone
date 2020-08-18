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

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendEmail {

    private final MailSender mailSender;
    private final AccountRepository accountRepository;

    public void sendSignupConfirmEmail(Account member) {
        EmailInfo emailInfo = EmailInfo.builder()
                .to(member.getEmail())
                .subject("STUDY GROUP SITE JOINED")
                .message("/check-email-token?" + "token=" + member.getEmailCheckToken() + "&" + "email=" + member.getEmail())
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
