package lec.spring.studygroupclone.Services;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Models.Location;
import lec.spring.studygroupclone.Models.Study;
import lec.spring.studygroupclone.Models.Tag;
import lec.spring.studygroupclone.Repositories.StudyRepository;
import lec.spring.studygroupclone.dataMappers.StudySetting;
import lec.spring.studygroupclone.helpers.Converter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        if( study == null ) throw new IllegalArgumentException("There is no study : " + path);
        return study;
    }

    public Study getStudyByPath(Account account, String path) {
        Study study = this.getStudyByPath(path);

        if( !study.isManager(account) ) throw new AccessDeniedException("Not enough permission");

        return study;
    }

    public Study getStudyByPath(Account account, String path, String mode) {
        Study study;
        switch (mode) {
            case "tagAndManager":
                study = studyRepository.findStudyWithTagsByPath(path);
                break;
            case "locationsAndManager":
                study = studyRepository.findStudyWithLocationsByPath(path);
                break;
            case "manager":
                study = studyRepository.findStudyWithManagersByPath(path);
                break;
            default:
                return this.getStudyByPath(account, path);
        }

        if( study == null ) throw new IllegalArgumentException("There is no study : " + path);

        if( !study.isManager(account) ) throw new AccessDeniedException("Not enough permission");

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

    public void updateLocation(Study study, Location location) {
        Optional<Study> studyResult = studyRepository.findById(study.getId());
        studyResult.ifPresent(s -> s.getLocations().add(location));
    }

    public void removeLocation(Study study, Location location) {
        Optional<Study> studyResult = studyRepository.findById(study.getId());
        studyResult.ifPresent(s -> s.getLocations().remove(location));
    }

    public String updateStatus(Study study, String status) {
        String nowStatus = null;
        if(status.equals("publish")){

            // TODO:: send EMAIL ALARM

            if ( !study.isClosed() && !study.isPublished() ) {
                study.setPublished(true);
                study.setPublishedDateTime(LocalDateTime.now());
                nowStatus = "published";
            }
            else throw new RuntimeException("The study is already open or closed.");
        } else if (status.equals("close")){

            // TODO:: send EMAIL ALARM

            if ( !study.isClosed() && study.isPublished() ) {
                study.setClosed(true);
                study.setClosedDateTime(LocalDateTime.now());
                nowStatus = "closed";
            }
            else throw new RuntimeException("The study is already closed or not opened.");
        } else if (status.equals("recruit")){

            // TODO:: send EMAIL ALARM

            if ( !study.isClosed() && study.isPublished() && study.canUpdateRecruiting() && !study.isRecruiting() ) {
                study.setRecruiting(true);
                study.setRecruitingUpdatedDateTime(LocalDateTime.now());
                nowStatus = "recruiting";
            } else if( !study.isClosed() && study.isPublished() && !study.isRecruiting()){
                throw new RuntimeException("You should wait " + study.getRemainAbleToUpdateRecruiting() + " minutes.");
            }
            else throw new RuntimeException("The study is closed or not opened or already recruiting.");
        } else if (status.equals("block")){

            // TODO:: send EMAIL ALARM

            if ( !study.isClosed() && study.isPublished() && study.canUpdateRecruiting() && study.isRecruiting()) {
                study.setRecruiting(false);
                study.setRecruitingUpdatedDateTime(LocalDateTime.now());
                nowStatus = "blocked";
            } else if( !study.isClosed() && study.isPublished() && study.isRecruiting()){
                throw new RuntimeException("You should wait " + study.getRemainAbleToUpdateRecruiting() + " minutes.");
            }
            else throw new RuntimeException("The study is closed or not opened or already blocked.");
        }

        return nowStatus;
    }
}
