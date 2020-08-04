package lec.spring.studygroupclone.helpers.account;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Services.AccountService;
import lec.spring.studygroupclone.config.AppConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Collections;

@RequiredArgsConstructor
public class WithFakeAccountForTestFactory implements WithSecurityContextFactory<WithFakeAccountForTest> {

    private final AccountService accountService;

    @Override
    public SecurityContext createSecurityContext(WithFakeAccountForTest fakeAccount) {
        String email = fakeAccount.email();

        Account account = new Account();
        account.setEmail(email);
        account.setNickname("sunday");
        account.setPassword(AppConfig.passwordEncoder().encode("security"));
        accountService.processSignUp(account);

        System.out.println("====================== create account ========================");

        UserDetails userDetails = accountService.loadUserByUsername(email);
        Authentication token = new UsernamePasswordAuthenticationToken(
                userDetails, //principal
                userDetails.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(token);
        return context;
    }
}
