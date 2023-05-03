package hcmute.puzzle.services;

import hcmute.puzzle.infrastructure.dtos.olds.CommentDto;
import hcmute.puzzle.infrastructure.models.response.DataResponse;

public interface CommentService {
    DataResponse save(CommentDto dto);

    DataResponse update(CommentDto dto, long id);

    DataResponse delete(long id);

    DataResponse getAll();

    DataResponse getOneById(long id);

    DataResponse likeComment(long id);

    DataResponse disLikeComment(long id);
}
