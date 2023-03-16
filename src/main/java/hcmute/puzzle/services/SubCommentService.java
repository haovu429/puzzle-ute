package hcmute.puzzle.services;

import hcmute.puzzle.dto.CategoryDTO;
import hcmute.puzzle.dto.SubCommentDTO;
import hcmute.puzzle.response.DataResponse;

public interface SubCommentService {

    DataResponse save(SubCommentDTO dto);

    DataResponse update(SubCommentDTO dto, long id);

    DataResponse delete(long id);

    DataResponse getAll();

    DataResponse getOneById(long id);
}
