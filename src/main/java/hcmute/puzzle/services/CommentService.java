package hcmute.puzzle.services;

import hcmute.puzzle.dto.CategoryDTO;
import hcmute.puzzle.dto.CommentDTO;
import hcmute.puzzle.response.DataResponse;

public interface CommentService {
    DataResponse save(CommentDTO dto);

    DataResponse update(CommentDTO dto, long id);

    DataResponse delete(long id);

    DataResponse getAll();

    DataResponse getOneById(long id);

    DataResponse likeComment(long id);

    DataResponse disLikeComment(long id);
}
