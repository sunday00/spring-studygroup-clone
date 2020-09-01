package lec.spring.studygroupclone.helpers.study;

import lec.spring.studygroupclone.Repositories.StudyRepository;
import lec.spring.studygroupclone.dataMappers.StudySetting;
import lec.spring.studygroupclone.helpers.PathAndUri;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class StudySettingValidator implements Validator{

    private final StudyRepository studyRepository;
    private final PathAndUri pathAndUri;

    @Override
    public boolean supports(Class<?> clazz) {
//        return clazz.isAssignableFrom(Study.class);
        return StudySetting.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        StudySetting study = (StudySetting) target;

        pathAndUri.setUri();

        if(studyRepository.existsByPath(study.getPath()) && pathAndUri.getMode().equals("create") ){
            errors.rejectValue("path", "wrong.path", "This path is taken others");
        }

        if( study.getPath() != null && studyRepository.existsByPath(study.getPath()) && pathAndUri.getUri().equals("/study/setting/path/" + pathAndUri.getLastPath()) ){
            errors.rejectValue("path", "wrong.path", "This path is taken others or not edited");
        }
    }
}
