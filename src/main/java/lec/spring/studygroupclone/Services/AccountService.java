package lec.spring.studygroupclone.Services;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Repositories.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final JavaMailSender javaMailSender;
    private final AccountRepository accountRepository;

    public void processSignUp(Account account){
        Account member = save(account);
        member.generateEmailCheckToken();
        sendSignupConfirmEmail(member);
    }

    private Account save(Account account) {
        return accountRepository.save(account);
    }

    private void sendSignupConfirmEmail(Account member) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(member.getEmail());
        simpleMailMessage.setSubject("STUDY GROUP SITE JOINED");
        simpleMailMessage.setText("/check-email-token?" +
                "token=" + member.getEmailCheckToken() + "&" +
                "email=" + member.getEmail());

        javaMailSender.send(simpleMailMessage);
    }
}
