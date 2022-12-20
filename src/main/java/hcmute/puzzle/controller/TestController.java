package hcmute.puzzle.controller;

import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.security.CustomUserDetails;
import hcmute.puzzle.test.SetUpDB;
import hcmute.puzzle.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api")
@CrossOrigin(origins = {Constant.LOCAL_URL, Constant.ONLINE_URL})
public class TestController {

  @Autowired SetUpDB setUpDB;

  @GetMapping("/init-db")
  public String getAll() {
    setUpDB.preStart();
    return "Done!";
  }

  @GetMapping("/test-get-input-method-post")
  ResponseObject followEmployer(
          @PathVariable(value = "id") Long employerId,
          // @RequestBody Map<String, Object> input,
          // @RequestParam(name = "employerId") long employerId,
          Authentication authentication) {
    // Map<String, Object> retMap = new HashMap<String, Object>();

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    // long candidateId = linkUser.get().getId();

    // https://stackoverflow.com/questions/58056944/java-lang-integer-cannot-be-cast-to-java-lang-long
    // long candidateId = ((Number) input.get("candidateId")).longValue();
    // long employerId = ((Number) input.get("employerId")).longValue();

    return null;
  }
}
