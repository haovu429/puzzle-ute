package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SubscribeRepository extends JpaRepository<SubscriptionEntity, Long> {
    @Query("SELECT sub FROM SubscriptionEntity sub, PackageEntity pack, UserEntity u WHERE sub.packageEntity.id = pack.id AND sub.regUser.id = u.id AND u.id=:userId AND pack.id =:packId")
    Optional<SubscriptionEntity> findSubByUserIdAndPackId(@Param("userId") long userId, @Param("packId") long packId);
}
