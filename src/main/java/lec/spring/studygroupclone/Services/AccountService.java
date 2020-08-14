package lec.spring.studygroupclone.Services;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

import lec.spring.studygroupclone.Models.Location;
import lec.spring.studygroupclone.Models.Tag;
import lec.spring.studygroupclone.helpers.Converter;
import lec.spring.studygroupclone.helpers.account.SendEmail;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
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

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    //depend other class
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;
    private final SendEmail sendEmail;

    @Value("${spring.profiles.active}")
    private String active;

    // convenient query
    public Long count() {
        return accountRepository.count();
    }
    public void deleteByEmail(String email) {
        accountRepository.deleteByEmail(email);
    }

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

    @Transactional(readOnly = true)
    public Account getAccountByEmail(String email) {
        Account user = accountRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("There is no user who has " + email + " as a email");
        }
        return user;
    }


    //signup and email token
    public void processSignUp(Account account) {
        account.setPassword(AppConfig.passwordEncoder().encode(account.getPassword()));
        account.setEmailVerified(false);
        accountRepository.save(account);
        account.generateEmailCheckToken();
        sendEmail.sendSignupConfirmEmail(account);

        this.login(account);
    }

    public Account checkEmailToken(String token, String email) {
        Account account = sendEmail.checkEmailToken(token, email);
        if( account == null ) return null;

        this.login(account);

        return account;
    }

    public boolean resendVerificationToken(Account account) {
        return sendEmail.resendVerificationToken(account, active);
    }


    // login
    private void login(Account account) {
        CurrentAccount currentAccount = new CurrentAccount(account);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(currentAccount, // principal
                account.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    // update profile(address, description, profile image), password, notification
    public void update(Account account, Profile profile) throws IOException {
        if( Converter.b64ToFile(account, profile) != null ) {
            account.setProfileImage("/uploads" + Converter.b64ToFile(account, profile));
        } else {
            account.setProfileImage(null);
        }

        modelMapper.map(profile, account);

        accountRepository.save(account);
    }

    public void updatePasswd(Account account, Profile profile) {
        account.setPassword(AppConfig.passwordEncoder().encode(profile.getNewPassword()));
        accountRepository.save(account);
    }

    public void updateAlarm(Account account, Profile profile) {
        modelMapper.map(profile, account);
        accountRepository.save(account);
    }

    public void updateTag(Account account, Tag tag) {
        Optional<Account> member = accountRepository.findById(account.getId());
        member.ifPresent(m -> m.getTags().add(tag));
    }

    public void removeTag(Account account, Tag tag) {
        Optional<Account> member = accountRepository.findById(account.getId());
        member.ifPresent(m -> m.getTags().remove(tag));
    }

    public void updateLocation(Account account, Location location) {
        Optional<Account> member = accountRepository.findById(account.getId());
        member.ifPresent(m -> m.getLocations().add(location));
    }

    public void removeLocation(Account account, Location location) {
        Optional<Account> member = accountRepository.findById(account.getId());
        member.ifPresent(m -> m.getLocations().remove(location));
    }

    public void updateAccount(Account account, Profile profile) {
        account.setNickname(profile.getNickname());
        accountRepository.save(account);
    }

    // forget password
    public HashMap<String, String> sendLoginToken(Account account) {
        HashMap<String, String> result = new HashMap<>();

        Account member = accountRepository.findByEmail(account.getEmail());

        if( member == null ){
            result.put("error", "Not found user. Register first.");
            return result;
        }

        if( !this.resendVerificationToken(member) ) {
            result.put("error", "You should wait " + member.getRemainAbleToResendEmailCheckToken() + " minutes to resend Verification Email.");
            return result;
        }

        result.put("info", "Sent login token. Check your email box.");

        return result;
    }
}
