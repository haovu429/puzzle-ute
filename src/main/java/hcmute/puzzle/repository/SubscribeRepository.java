package hcmute.puzzle.repository;

import hcmute.puzzle.entities.SubscribeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SubscribeRepository extends JpaRepository<SubscribeEntity, Long> {
    @Query("SELECT sub FROM SubscribeEntity sub, PackageEntity pack, UserEntity u WHERE sub.packageEntity.id = pack.id AND sub.regUser.id = u.id AND u.id=:userId AND pack.id =:packId")
    Optional<SubscribeEntity> findSubByUserIdAndPackId(@Param("userId") long userId, @Param("packId") long packId);
}
