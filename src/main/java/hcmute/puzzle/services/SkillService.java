package hcmute.puzzle.services;

import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.dto.SkillDTO;

public interface SkillService {
  ResponseObject save(SkillDTO skillDTO);

  ResponseObject update(SkillDTO skillDTO);

  ResponseObject delete(long id);

  ResponseObject getAll();
}
