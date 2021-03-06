package lec.spring.studygroupclone.Repositories;

import lec.spring.studygroupclone.Models.Event;
import lec.spring.studygroupclone.Models.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public interface EventRepository extends JpaRepository<Event, Long> {

    @Transactional(readOnly = true)
    List<Event> findAllByStudy(Study study);

    @Transactional(readOnly = true)
    @EntityGraph(value = "Event.withEnrollments", type = EntityGraph.EntityGraphType.LOAD)
    List<Event> findAllByStudyOrderByStartAt(Study study);

    @Transactional(readOnly = true)
    @EntityGraph(value = "Event.withEnrollments", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT e FROM Event e WHERE e.endAt <= :now AND e.study = :study")
    List<Event> findAllWithEndAtBeforeNowByStudy(LocalDateTime now, Study study);

    @Transactional(readOnly = true)
    @EntityGraph(value = "Event.withEnrollments", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT e FROM Event e WHERE e.endAt >= :now AND e.study = :study")
    List<Event> findAllWithEndAtAfterNowByStudy(LocalDateTime now, Study study);

    @Transactional(readOnly = true)
    Integer countByEndAtAfterAndStudy(LocalDateTime now, Study study);

    @Transactional(readOnly = true)
    Integer countByEndAtBeforeAndStudy(LocalDateTime now, Study study);
}
