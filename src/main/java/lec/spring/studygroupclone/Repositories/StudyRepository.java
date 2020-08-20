package lec.spring.studygroupclone.Repositories;

import lec.spring.studygroupclone.Models.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface StudyRepository extends JpaRepository<Study, Long> {

    @Transactional(readOnly = true)
    boolean existsByPath(String path);

    @Transactional(readOnly = true)
    @EntityGraph(value = "Study.withAll", type = EntityGraph.EntityGraphType.LOAD)
    Study findByPath(String path);
}
