package lec.spring.studygroupclone.helpers;

import lec.spring.studygroupclone.Models.Account;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

@Getter
public class CurrentAccount extends User {

    private Account account;

    public CurrentAccount(Account account) {
        super(account.getEmail(), account.getPassword(), Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        this.account = account;
    }
}
