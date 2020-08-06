package lec.spring.studygroupclone.helpers.account;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Repositories.AccountRepository;
import lec.spring.studygroupclone.dataMappers.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class ProfileValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(Profile.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Profile profile = (Profile) target;

        if( profile.getNewPassword() != null && !profile.getNewPassword().equals(profile.getNewPasswordConfirm()) ){
            errors.rejectValue("newPassword",
                    "invalid.newPassword",
                    new Object[]{profile.getNewPassword()},
                    "password confirmation is not equals to password"
            );
            errors.rejectValue("newPasswordConfirm",
                    "invalid.newPasswordConfirm",
                    new Object[]{profile.getNewPasswordConfirm()},
                    "password confirmation is not equals to password"
            );
        }
    }
}
