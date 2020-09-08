package lec.spring.studygroupclone.Repositories;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Models.Enrollment;
import lec.spring.studygroupclone.Models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    @Transactional(readOnly = true)
    Enrollment findByAccount(Account account);

    @Transactional(readOnly = true)
    Enrollment findByAccountAndEvent(Account account, Event event);

    void deleteByEventAndAccount(Event event, Account account);

    @Modifying
    @Query("UPDATE Enrollment e SET e.accepted = true WHERE e IN (:enrollments)")
    void updateByIdInList(List<Enrollment> enrollments);

    List<Enrollment> findAllByEvent(Event event);
}
