package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

// @Scope(value = "singleton")
public interface RoleRepository extends JpaRepository<Role, String> {
  Role findOneByCode(String code);

  Optional<Role> findByCode(String code);

  List<Role> findAllByCodeIn(Collection<String> code);
}
//
