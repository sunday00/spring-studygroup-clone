package lec.spring.studygroupclone.Repositories;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Models.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
@Transactional
public interface StudyRepository extends JpaRepository<Study, Long>, StudyRepositoryExtension {

    @Transactional(readOnly = true)
    boolean existsByPath(String path);

    @Transactional(readOnly = true)
    @EntityGraph(value = "Study.withAll", type = EntityGraph.EntityGraphType.LOAD)
    Study findByPath(String path);

    @Transactional(readOnly = true)
    @EntityGraph(value = "Study.withTagsAndManagers", type = EntityGraph.EntityGraphType.FETCH)
    Study findStudyWithTagsByPath(String path);

    @Transactional(readOnly = true)
    @EntityGraph(value = "Study.withLocationsAndManagers", type = EntityGraph.EntityGraphType.FETCH)
    Study findStudyWithLocationsByPath(String path);

    @Transactional(readOnly = true)
    @EntityGraph(value = "Study.withTagsAndLocations", type = EntityGraph.EntityGraphType.FETCH)
    Study findWithTagsAndLocationsById(Long id);

    @Transactional(readOnly = true)
    @EntityGraph(value = "Study.withManagers", type = EntityGraph.EntityGraphType.FETCH)
    Study findStudyWithManagersByPath(String path);

    @Transactional(readOnly = true)
    @EntityGraph(value = "Study.withMembers", type = EntityGraph.EntityGraphType.FETCH)
    Study findStudyWithMembersByPath(String path);

    @Transactional(readOnly = true)
    @EntityGraph(attributePaths = {"managers", "members"})
    Study findStudyWithManagersAndMembersById(Long id);

    @Transactional(readOnly = true)
    @EntityGraph(value = "Study.only", type = EntityGraph.EntityGraphType.FETCH)
    Study findStudyByPath(String path);

    @Transactional(readOnly = true)
    @OrderBy(value = "createdAt DESC")
    Set<Study> findAllByMembersContains(Account account);

    @Transactional(readOnly = true)
    @OrderBy(value = "createdAt DESC")
    Set<Study> findAllByManagersContains(Account account);
}
