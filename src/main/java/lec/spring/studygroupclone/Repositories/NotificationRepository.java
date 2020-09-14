package lec.spring.studygroupclone.Repositories;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OrderBy;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Long countByAccountAndChecked(Account account, boolean checked);

    @OrderBy("created_at DESC")
    List<Notification> findByAccountAndChecked(Account account, boolean checked);

    Notification findByAccountAndId(Account account, Long id);
}
