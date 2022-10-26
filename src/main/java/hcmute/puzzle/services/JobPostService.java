package hcmute.puzzle.services;

import hcmute.puzzle.dto.CandidateDTO;
import hcmute.puzzle.dto.JobPostDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.entities.JobPostEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;


public interface JobPostService {
    Optional<JobPostDTO> add(JobPostDTO jobPostDTO);

    ResponseObject delete(long id);

    ResponseObject update(JobPostDTO jobPostDTO);

    ResponseObject getOne(long id);

    void validateJobPost(JobPostDTO jobPostDTO);

    ResponseObject getAll();
}
