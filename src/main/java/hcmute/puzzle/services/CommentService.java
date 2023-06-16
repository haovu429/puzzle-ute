package hcmute.puzzle.services;

import hcmute.puzzle.infrastructure.dtos.news.CreateCommentRequest;
import hcmute.puzzle.infrastructure.dtos.olds.CommentDto;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
//    DataResponse save(CommentDto dto);

    CommentDto update(CommentDto dto, long id);

    void delete(long id);

    Page<CommentDto> getAll(Pageable pageable);

    CommentDto getOneById(long id);

    void likeComment(long id);

    void disLikeComment(long id);

    CommentDto addComment(CreateCommentRequest createCommentRequest, long blogPostId);
}
