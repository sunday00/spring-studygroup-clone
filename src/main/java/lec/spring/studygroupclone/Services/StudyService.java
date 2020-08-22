package lec.spring.studygroupclone.Services;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Models.Study;
import lec.spring.studygroupclone.Repositories.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {
    private final StudyRepository studyRepository;


    public Study create(Study study, Account account) {
        Study newStudy = studyRepository.save(study);
        newStudy.addManager(account);
        return newStudy;
    }

    public Study getStudyByPath(String path) {
        Study study = studyRepository.findByPath(path);

        if( study == null ){
             throw new IllegalArgumentException("There is no trudy : " + path);
        }

        return study;
    }
}
