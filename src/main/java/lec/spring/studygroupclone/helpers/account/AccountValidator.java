package lec.spring.studygroupclone.helpers.account;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Repositories.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class AccountValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(Account.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Account account = (Account) target;

        if( accountRepository.existsByEmail(account.getEmail()) ){
            errors.rejectValue("email",
                    "invalid.email",
                    new Object[]{account.getEmail()},
                    "This email is already Joined our site."
            );
        }

        if( accountRepository.existsByNickname(account.getNickname()) ){
            errors.rejectValue("nickname",
                    "invalid.nickname",
                    new Object[]{account.getNickname()},
                    "This nickname is already taken by other person."
            );
        }
    }
}
