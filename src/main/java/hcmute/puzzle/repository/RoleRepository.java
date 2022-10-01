package hcmute.puzzle.repository;

import hcmute.puzzle.entities.RoleEntity;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;

@Scope(value = "singleton")
public interface RoleRepository extends JpaRepository<RoleEntity, String> {
  RoleEntity findOneByCode(String code);
}
//
