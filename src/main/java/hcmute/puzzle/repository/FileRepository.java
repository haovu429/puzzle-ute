package hcmute.puzzle.repository;

import hcmute.puzzle.entities.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    @Query("SELECT f FROM FileEntity f WHERE f.cloudinaryPublicId in :cloudinaryPublicIds AND f.isDeleted =:deleted")
    List<FileEntity> findAllByCloudinaryPublicIdInAndDeletedIs(Collection<String> cloudinaryPublicIds, boolean deleted);

    @Query("SELECT f FROM FileEntity f WHERE f.url in :urls AND f.isDeleted =:deleted")
    List<FileEntity> findAllByUrlInAndDeletedIs(Collection<String> urls, boolean deleted);

    @Query("SELECT f FROM FileEntity f WHERE f.url = :urls AND f.isDeleted =:deleted")
    Optional<FileEntity> findAllByUrlAndDeletedIs(String url, boolean deleted);

    Optional<FileEntity> findByCloudinaryPublicId(String cloudinaryPublicId);
}
