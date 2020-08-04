package lec.spring.studygroupclone.helpers.account;

import lec.spring.studygroupclone.dataMappers.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class ProfileValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(Profile.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

    }
}
