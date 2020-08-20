package lec.spring.studygroupclone.helpers.study;

import lec.spring.studygroupclone.Repositories.StudyRepository;
import lec.spring.studygroupclone.dataMappers.StudySetting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class StudySettingValidator implements Validator{

    private final StudyRepository studyRepository;

    @Override
    public boolean supports(Class<?> clazz) {
//        return clazz.isAssignableFrom(Study.class);
        return StudySetting.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        StudySetting study = (StudySetting) target;

        if(studyRepository.existsByPath(study.getPath())){
            errors.rejectValue("path", "wrong.path", "This path is taken others");
        }

    }
}
