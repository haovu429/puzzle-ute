package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    @Query("SELECT f FROM File f WHERE f.cloudinaryPublicId in :cloudinaryPublicIds AND f.isDeleted =:deleted")
    List<File> findAllByCloudinaryPublicIdInAndDeletedIs(Collection<String> cloudinaryPublicIds, boolean deleted);

    @Query("SELECT f FROM File f WHERE f.url in :urls AND f.isDeleted =:deleted")
    List<File> findAllByUrlInAndDeletedIs(Collection<String> urls, boolean deleted);

    @Query("SELECT f FROM File f WHERE f.url = :url AND f.isDeleted =:deleted")
    Optional<File> findAllByUrlAndDeletedIs(String url, boolean deleted);

    Optional<File> findByCloudinaryPublicId(String cloudinaryPublicId);
}
