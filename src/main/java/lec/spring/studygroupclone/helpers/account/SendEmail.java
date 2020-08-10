package lec.spring.studygroupclone.helpers.account;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Repositories.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SendEmail {

    private final JavaMailSender javaMailSender;
    private final AccountRepository accountRepository;

    public void sendSignupConfirmEmail(Account member) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(member.getEmail());

        simpleMailMessage.setSubject("STUDY GROUP SITE JOINED");
        simpleMailMessage.setText(
                "/check-email-token?" + "token=" + member.getEmailCheckToken() + "&" + "email=" + member.getEmail()
        );


        javaMailSender.send(simpleMailMessage);
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
