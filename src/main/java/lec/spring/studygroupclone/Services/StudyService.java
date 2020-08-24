package lec.spring.studygroupclone.Services;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Models.Study;
import lec.spring.studygroupclone.Models.Tag;
import lec.spring.studygroupclone.Repositories.AccountRepository;
import lec.spring.studygroupclone.Repositories.StudyRepository;
import lec.spring.studygroupclone.dataMappers.StudySetting;
import lec.spring.studygroupclone.helpers.Converter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {
    private final StudyRepository studyRepository;
    private final ModelMapper modelMapper;

    public Study create(Study study, Account account) {
        Study newStudy = studyRepository.save(study);
        newStudy.addManager(account);
        return newStudy;
    }

    public Study getStudyByPath(String path) {
        Study study = studyRepository.findByPath(path);

        if( study == null ){
             throw new IllegalArgumentException("There is no study : " + path);
        }

        return study;
    }

    public void save(Study study, StudySetting studySetting) {
        modelMapper.map(studySetting, study);
    }

    public void setStudyToggleBannerUsing(Study study, String enable) {
        study.setUseBanner(enable.equals("true"));
    }

    public void setStudyBannerImage(Study study, String image) throws IOException {
        study.setImage("/uploads" + Converter.b64ToFile(study, image));
    }

    public void updateTag(Study study, Tag tag) {
        Optional<Study> studyResult = studyRepository.findById(study.getId());
        studyResult.ifPresent(s -> s.getTags().add(tag));
    }

    public void removeTag(Study study, Tag tag) {
        Optional<Study> studyResult = studyRepository.findById(study.getId());
        studyResult.ifPresent(s -> s.getTags().remove(tag));
    }
}
