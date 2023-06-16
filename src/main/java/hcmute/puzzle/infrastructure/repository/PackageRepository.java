package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.Package;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PackageRepository extends JpaRepository<Package, Long> {

    Optional<Package> findByCode(String code);
}
