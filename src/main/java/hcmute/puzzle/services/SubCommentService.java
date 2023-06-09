package hcmute.puzzle.services;

import hcmute.puzzle.infrastructure.dtos.olds.SubCommentDto;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;

public interface SubCommentService {

    DataResponse save(SubCommentDto dto);

    DataResponse update(SubCommentDto dto, long id);

    DataResponse delete(long id);

    DataResponse getAll();

    DataResponse getOneById(long id);
}
