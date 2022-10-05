package hcmute.puzzle.response;

import hcmute.puzzle.entities.ApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<ApplicationEntity, Long> {

}
