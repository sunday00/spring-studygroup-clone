package lec.spring.studygroupclone.Services;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;

import lec.spring.studygroupclone.helpers.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Repositories.AccountRepository;
import lec.spring.studygroupclone.config.AppConfig;
import lec.spring.studygroupclone.dataMappers.CurrentAccount;
import lec.spring.studygroupclone.dataMappers.Profile;
import lombok.RequiredArgsConstructor;

import javax.imageio.ImageIO;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final JavaMailSender javaMailSender;
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    public void processSignUp(Account account) {
        account.setPassword(AppConfig.passwordEncoder().encode(account.getPassword()));
        account.setEmailVerified(false);
        save(account);
        account.generateEmailCheckToken();
        sendSignupConfirmEmail(account);

        this.login(account);
    }

    private Account save(Account account) {
        return accountRepository.save(account);
    }

    private void sendSignupConfirmEmail(Account member) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(member.getEmail());
        simpleMailMessage.setSubject("STUDY GROUP SITE JOINED");
        simpleMailMessage.setText(
                "/check-email-token?" + "token=" + member.getEmailCheckToken() + "&" + "email=" + member.getEmail());

        javaMailSender.send(simpleMailMessage);
    }

    public Account checkEmailToken(String token, String email) {
        Account account = accountRepository.findByEmail(email);
        if (account == null || !account.getEmailCheckToken().equals(token))
            return null;

        account.setEmailVerified(true);
        account.setJoinedAt(LocalDateTime.now());
        this.save(account);

        this.login(account);

        return account;
    }

    public Long count() {
        return accountRepository.count();
    }

    private void login(Account account) {
        CurrentAccount currentAccount = new CurrentAccount(account);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(currentAccount, // principal
                account.getPassword(),
                // List.of(new SimpleGrantedAuthority("ROLE_USER"))
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    public boolean resendVerificationToken(Account account) {
        Account member = accountRepository.findByEmail(account.getEmail());
        if (member.canSendEmailCheckToken()) {
            member.generateEmailCheckToken();
            member = this.save(member);
            sendSignupConfirmEmail(member);
            return true;
        }
        return false;
    }

    // public boolean signIn(Account account) {
    // Account target = accountRepository.findByEmail(account.getEmail());
    // if(target == null) return false;
    //
    // boolean isUser = appConfig.passwordEncoder().matches(account.getPassword(),
    // target.getPassword());
    //
    // if(isUser) {
    // this.login(target);
    // return true;
    // }
    //
    // return false;
    // }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(username);

        if (account == null) {
            throw new UsernameNotFoundException(username);
        }

        return new CurrentAccount(account);
    }

    @Transactional(readOnly = true)
    public Account getAccount(String nickname) {
        Account user = accountRepository.findByNickname(nickname);
        if (user == null) {
            throw new IllegalArgumentException("There is no user who has " + nickname + " as a nickName");
        }
        return user;
    }

    public void update(Account account, Profile profile) throws IOException {
        if( Converter.b64ToFile(account, profile) != null ) {
            account.setProfileImage("/uploads" + Converter.b64ToFile(account, profile));
        } else {
            account.setProfileImage(null);
        }

        modelMapper.map(profile, account);

        this.save(account);
    }

    public void updatePasswd(Account account, Profile profile) {
        account.setPassword(AppConfig.passwordEncoder().encode(profile.getNewPassword()));
        this.save(account);
    }

    public void updateAlarm(Account account, Profile profile) {
        modelMapper.map(profile, account);
        this.save(account);
    }

    public void deleteByEmail(String email) {
        accountRepository.deleteByEmail(email);
    }
}
