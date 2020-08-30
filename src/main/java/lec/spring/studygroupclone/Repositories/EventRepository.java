package lec.spring.studygroupclone.Repositories;

import lec.spring.studygroupclone.Models.Event;
import lec.spring.studygroupclone.Models.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface EventRepository extends JpaRepository<Event, Long> {

    @Transactional(readOnly = true)
    List<Event> findAllByStudy(Study study);
}
