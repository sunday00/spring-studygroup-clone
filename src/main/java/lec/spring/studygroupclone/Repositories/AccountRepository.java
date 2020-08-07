package lec.spring.studygroupclone.Repositories;

import lec.spring.studygroupclone.Models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Transactional(readOnly = true)
    boolean existsByEmail(String email);

    @Transactional(readOnly = true)
    boolean existsByNickname(String nickname);

    @Transactional(readOnly = true)
    Account findByEmail(String email);

    @Transactional(readOnly = true)
    Account findByNickname(String nickname);

    void deleteByNickname(String nickname);

    void deleteByEmail(String email);
}
