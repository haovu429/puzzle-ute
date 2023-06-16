package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SubscribeRepository extends JpaRepository<Subscription, Long> {
    @Query("SELECT sub FROM Subscription sub, Package pack, User u WHERE sub.aPackage.id = pack.id AND sub.regUser.id = u.id AND u.id=:userId AND pack.id =:packId")
    Optional<Subscription> findSubByUserIdAndPackId(@Param("userId") long userId, @Param("packId") long packId);
}
