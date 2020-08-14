package lec.spring.studygroupclone.Repositories;

import lec.spring.studygroupclone.Models.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface LocationRepository extends JpaRepository<Location, Long> {

    @Transactional(readOnly = true)
    Location findByCity(String city);
}
