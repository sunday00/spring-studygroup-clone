package lec.spring.studygroupclone.Services;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Repositories.AccountRepository;
import lec.spring.studygroupclone.config.SecurityConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final JavaMailSender javaMailSender;
    private final AccountRepository accountRepository;
    private final SecurityConfig securityConfig;

    @Transactional
    public void processSignUp(Account account){
        account.setPassword( securityConfig.passwordEncoder().encode(account.getPassword()) );
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

    public Account checkEmailToken(String token, String email) {
        Account account = accountRepository.findByEmail(email);
        if( account == null || !account.getEmailCheckToken().equals(token) ) return null;

        account.setEmailVerified(true);
        account.setJoinedAt(LocalDateTime.now());

        return account;
    }

    public Long count() {
        return accountRepository.count();
    }
}
