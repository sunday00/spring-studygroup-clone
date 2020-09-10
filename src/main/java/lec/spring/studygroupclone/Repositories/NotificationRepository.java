package lec.spring.studygroupclone.Repositories;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Long countByAccountAndChecked(Account account, boolean checked);
}
