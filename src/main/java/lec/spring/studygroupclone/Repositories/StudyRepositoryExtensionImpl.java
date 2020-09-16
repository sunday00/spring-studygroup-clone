package lec.spring.studygroupclone.Repositories;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPQLQuery;
import lec.spring.studygroupclone.Models.*;
import lec.spring.studygroupclone.helpers.ConsoleLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Objects;

public class StudyRepositoryExtensionImpl extends QuerydslRepositorySupport implements StudyRepositoryExtension{

    public StudyRepositoryExtensionImpl() {
        super(Study.class);
    }

    @Override
    public Page<Study> findByKeyword(String keyword, Pageable pageable) {
        QStudy study = QStudy.study;

        JPQLQuery<Study> query = from(study)
                .select(study)
            .where(
                study.published.isTrue()
                .and(study.title.containsIgnoreCase(keyword))
                .or(study.tags.any().title.containsIgnoreCase(keyword))
                .or(study.locations.any().city.containsIgnoreCase(keyword))
                .or(study.locations.any().localName.containsIgnoreCase(keyword))
            ).leftJoin(study.tags, QTag.tag).fetchJoin()
            .leftJoin(study.locations, QLocation.location).fetchJoin()
//            .leftJoin(study.members, QAccount.account).fetchJoin()
            .distinct();
//            .orderBy(new OrderSpecifier<>(Order.DESC, study.publishedDateTime));
        QueryResults<Study> studyQueryResults = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query).fetchResults();
        Page<Study> studies = new PageImpl<>(studyQueryResults.getResults(), pageable, studyQueryResults.getTotal());

//        JPQLQuery<Tuple> queryC = from(study).select(study.id, study.title, study.members.size())
//                .where(study.in( studies.getContent() ))
//                .groupBy(study);
//        QueryResults<Tuple> membersQueryResults = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, queryC).fetchResults();
//        Page<Tuple> countMember = new PageImpl<>(membersQueryResults.getResults(), pageable, membersQueryResults.getTotal());

        return studies;
    }
}
