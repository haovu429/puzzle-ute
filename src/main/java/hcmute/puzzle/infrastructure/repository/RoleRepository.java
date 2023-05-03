package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

// @Scope(value = "singleton")
public interface RoleRepository extends JpaRepository<RoleEntity, String> {
  RoleEntity findOneByCode(String code);

  Optional<RoleEntity> findByCode(String code);

  List<RoleEntity> findAllByCodeIn(Collection<String> code);
}
//
