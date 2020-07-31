package lec.spring.studygroupclone.Services;

import java.util.Collections;
import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Repositories.AccountRepository;
import lec.spring.studygroupclone.config.SecurityConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        account.setEmailVerified(false);
        Account member = save(account);
        member.generateEmailCheckToken();
        sendSignupConfirmEmail(member);

        this.login(member);
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

        this.login(account);

        return account;
    }

    public Long count() {
        return accountRepository.count();
    }

    private void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                account, //principal
                account.getPassword(),
//                List.of(new SimpleGrantedAuthority("ROLE_USER"))
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    public boolean resendVerificationToken(Account account) {
        Account member = accountRepository.findByEmail(account.getEmail());
        if( member.canSendEmailCheckToken() ) {
            member.generateEmailCheckToken();
            member = save(member);
            sendSignupConfirmEmail(member);
            return true;
        }
        return false;
    }

    public boolean signIn(Account account) {
        Account target = accountRepository.findByEmail(account.getEmail());
        if(target == null) return false;

        boolean isUser = securityConfig.passwordEncoder().matches(account.getPassword(), target.getPassword());

        if(isUser) {
            this.login(target);
            return true;
        }

        return false;
    }

//    implements UserDetailsService
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Account account = accountRepository.findByEmail(username);
//        return new User(account.getEmail(), account.getPassword(), Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
//    }

}
