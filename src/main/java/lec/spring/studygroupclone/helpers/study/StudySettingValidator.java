package lec.spring.studygroupclone.helpers.study;

import lec.spring.studygroupclone.Models.Study;
import lec.spring.studygroupclone.Repositories.StudyRepository;
import lec.spring.studygroupclone.dataMappers.StudySetting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;

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

        ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequestUri();
        String uri = builder.build().toUri().getPath();
        String[] uriSplit = uri.split("/");

        if(studyRepository.existsByPath(study.getPath()) && uriSplit[uriSplit.length - 1].equals("create") ){
            errors.rejectValue("path", "wrong.path", "This path is taken others");
        }

        if( study.getPath() != null && studyRepository.existsByPath(study.getPath()) && uri.equals("/study/setting/path/" + uriSplit[uriSplit.length - 1]) ){
            errors.rejectValue("path", "wrong.path", "This path is taken others");
        }
    }
}
