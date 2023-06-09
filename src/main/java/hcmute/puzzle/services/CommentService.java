package hcmute.puzzle.services;

import hcmute.puzzle.infrastructure.dtos.news.CreateCommentRequest;
import hcmute.puzzle.infrastructure.dtos.olds.CommentDto;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;

public interface CommentService {
    DataResponse save(CommentDto dto);

    DataResponse update(CommentDto dto, long id);

    DataResponse delete(long id);

    DataResponse getAll();

    DataResponse getOneById(long id);

    DataResponse likeComment(long id);

    DataResponse disLikeComment(long id);

    CommentDto addComment(CreateCommentRequest createCommentRequest, long blogPostId);
}
