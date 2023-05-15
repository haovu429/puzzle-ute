package hcmute.puzzle.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import hcmute.puzzle.configuration.security.CustomUserDetails;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.models.payload.request.TokenObject;
import hcmute.puzzle.infrastructure.models.response.DataResponse;
import hcmute.puzzle.infrastructure.repository.RoleRepository;
import hcmute.puzzle.infrastructure.repository.UserRepository;
import hcmute.puzzle.test.SetUpDB;
import hcmute.puzzle.test.TestCloudinary;
import hcmute.puzzle.utils.Constant;
import hcmute.puzzle.utils.firebase.FirebaseMessagingService;
import hcmute.puzzle.utils.firebase.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/test")
@CrossOrigin(origins = {Constant.LOCAL_URL, Constant.ONLINE_URL})
public class TestController {

	private final FirebaseMessagingService firebaseService;
	@Autowired
	SetUpDB setUpDB;
	@Autowired
	UserRepository userRepository;
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	TestCloudinary testCloudinary;

	public TestController(FirebaseMessagingService firebaseService) {
		this.firebaseService = firebaseService;
	}

	@GetMapping("/init-db")
	public String initDB() {
		setUpDB.preStart();
		setUpDB.createMdFileType();
		//    Optional<UserEntity> userEntity = userRepository.findByEmail("admin1@gmail.com");
		//    Optional<RoleEntity> role = roleRepository.findById("user");
		//    System.out.println(role.get().getName());
		//    System.out.println(userEntity.get().getEmail());
		//    userEntity.get().getRoles().add(role.get());
		//    userRepository.save(userEntity.get());
		//testCloudinary.testDeleteManyFileCloudinary();
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

	@PostMapping("/send-notification")
	@ResponseBody
	public String sendNotification(@RequestBody Note note, @RequestParam String topic)
			throws FirebaseMessagingException {
		return firebaseService.sendNotificationWithTopic(note, topic);
	}

	@PostMapping("/save-token-client")
	@ResponseBody
	public DataResponse saveTokenClient(@RequestBody TokenObject token) {
		System.out.println("Token = " + token);
		return new DataResponse("OK");
	}
}
