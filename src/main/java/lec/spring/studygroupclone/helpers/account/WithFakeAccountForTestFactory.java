package lec.spring.studygroupclone.helpers.account;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Models.Tag;
import lec.spring.studygroupclone.Repositories.TagRepository;
import lec.spring.studygroupclone.Services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class WithFakeAccountForTestFactory implements WithSecurityContextFactory<WithFakeAccountForTest> {

    private final AccountService accountService;
    private final TagRepository tagRepository;

    @Override
    public SecurityContext createSecurityContext(WithFakeAccountForTest fakeAccount) {

        Tag tag = new Tag();
        tag.setTitle("php");
        tagRepository.save(tag);

        Tag tag2 = new Tag();
        tag2.setTitle("java");
        tagRepository.save(tag2);

        String email = fakeAccount.email();

        Account account = new Account();
        account.setEmail(email);
        account.setNickname("sunday");
        account.setPassword("security");

        Set<Tag> accountTag = new HashSet<>();
        accountTag.add(tag);
        account.setTags(accountTag);

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
