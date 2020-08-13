package lec.spring.studygroupclone.Repositories;

import lec.spring.studygroupclone.Models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface TagRepository extends JpaRepository<Tag, Long> {

    @Transactional(readOnly = true)
    Tag findByTitle(String title);
}
