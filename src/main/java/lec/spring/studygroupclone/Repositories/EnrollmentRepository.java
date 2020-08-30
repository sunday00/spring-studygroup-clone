package lec.spring.studygroupclone.Repositories;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Models.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    @Transactional(readOnly = true)
    Enrollment findByAccount(Account account);
}
