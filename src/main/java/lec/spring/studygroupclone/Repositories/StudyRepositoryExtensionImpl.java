package lec.spring.studygroupclone.Repositories;

import lec.spring.studygroupclone.Models.QStudy;
import lec.spring.studygroupclone.Models.Study;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class StudyRepositoryExtensionImpl extends QuerydslRepositorySupport implements StudyRepositoryExtension{

    public StudyRepositoryExtensionImpl() {
        super(Study.class);
    }

    @Override
    public List<Study> findByKeyword(String keyword) {
        QStudy study = QStudy.study;
        return from(study).where(
                study.published.isTrue().and(study.title.containsIgnoreCase(keyword))
                    .or(study.tags.any().title.containsIgnoreCase(keyword))
                    .or(study.locations.any().city.containsIgnoreCase(keyword))
                    .or(study.locations.any().localName.containsIgnoreCase(keyword))
        ).fetch();
    }
}
