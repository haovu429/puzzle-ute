package hcmute.puzzle.services;

import hcmute.puzzle.dto.ResponseObject;
import org.springframework.data.domain.Pageable;

public interface ApplicationService {
  ResponseObject findById(Long id);

  ResponseObject deleteById(Long id);

  ResponseObject findAll(Pageable pageable);

  ResponseObject applyJobPost(long candidateId, long jobPostId);

  ResponseObject responseApplication(long applicationId, boolean isAccept, String note);

}
