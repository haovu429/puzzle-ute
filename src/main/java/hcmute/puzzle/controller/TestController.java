package hcmute.puzzle.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import hcmute.puzzle.configuration.security.CustomUserDetails;
import hcmute.puzzle.infrastructure.dtos.olds.CommentDto;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.entities.Comment;
import hcmute.puzzle.infrastructure.mappers.CommentMapper;
import hcmute.puzzle.infrastructure.mappers.SubCommentMapper;
import hcmute.puzzle.infrastructure.models.payload.request.TokenObject;
import hcmute.puzzle.infrastructure.models.response.DataResponse;
import hcmute.puzzle.infrastructure.repository.*;
import hcmute.puzzle.test.SetUpDB;
import hcmute.puzzle.test.TestCloudinary;
import hcmute.puzzle.utils.Constant;
import hcmute.puzzle.utils.firebase.FirebaseMessagingService;
import hcmute.puzzle.utils.firebase.Note;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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

	@Autowired
	SubCommentRepository subCommentRepository;

	@Autowired
	CommentMapper commentMapper;

	@Autowired
	CommentRepository commentRepository;

	@Autowired
	BlogPostRepository blogPostRepository;

	// inject for comment mapper use
	@Autowired
	SubCommentMapper subCommentMapper;

	public TestController(FirebaseMessagingService firebaseService) {
		this.firebaseService = firebaseService;
	}

	@RequestMapping(
			path = "/converter/sub-comment/{subCommentId}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity converterSubComment(@PathVariable long commentId) {

		Comment comment = commentRepository.findById(commentId).orElse(null);
		CommentDto commentDto = commentMapper.commentToCommentDto(comment);
		return ResponseEntity.ok().body(commentDto);
	}

	class LuckyNumber {
		Integer num;
		String reward;

		public LuckyNumber(Integer num, String reward) {
			this.num = num;
			this.reward = reward;
		}

	}

	@RequestMapping(
			path = "/compare",
			method = RequestMethod.GET,
			produces = MediaType.TEXT_PLAIN_VALUE
	)
	public String testMapAndForEach() {
		List<LuckyNumber> intList = new ArrayList<>();
		intList.add(new LuckyNumber(1, "House"));
		intList.add(new LuckyNumber(2, "Cash"));
		intList.add(new LuckyNumber(3, "Duck"));
		intList = intList.stream().map(number -> {
			if (number.num == 2){
				return new LuckyNumber(99, "car");
			} else {
				return new LuckyNumber(0, "fail");
			}
		}).collect(Collectors.toList());

		intList.forEach(number -> {
			if (number.num == 2){
				number.num =99;
			} else {
				number.num =0;
			}
		});
		return "OK";
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
