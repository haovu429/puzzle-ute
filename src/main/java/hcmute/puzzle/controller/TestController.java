package hcmute.puzzle.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import hcmute.puzzle.configuration.security.CustomUserDetails;
import hcmute.puzzle.infrastructure.dtos.olds.CommentDto;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.dtos.request.TokenObject;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
import hcmute.puzzle.infrastructure.entities.Comment;
import hcmute.puzzle.infrastructure.mappers.CommentMapper;
import hcmute.puzzle.infrastructure.mappers.SubCommentMapper;
import hcmute.puzzle.infrastructure.models.enums.FileCategory;
import hcmute.puzzle.infrastructure.repository.*;
import hcmute.puzzle.services.impl.AmazoneBucketService;
import hcmute.puzzle.test.SetUpDB;
import hcmute.puzzle.test.TestCloudinary;
import hcmute.puzzle.utils.Constant;
import hcmute.puzzle.utils.firebase.FirebaseMessagingService;
import hcmute.puzzle.utils.firebase.Note;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
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

	@Autowired
	AmazoneBucketService amazoneBucketService;

	public TestController(FirebaseMessagingService firebaseService) {
		this.firebaseService = firebaseService;
	}

	@RequestMapping(path = "/converter/sub-comment/{subCommentId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
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

	@Transactional
	@GetMapping("/init-db")
	public String initDB() {
		// setUpDB.tempRun();
		setUpDB.preStart();
		setUpDB.createMdFileType();
		setUpDB.initPackage();
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
	public String sendNotification(@RequestBody Note note, @RequestParam String topic) throws
			FirebaseMessagingException {
		return firebaseService.sendNotificationWithTopic(note, topic);
	}

	@PostMapping("/save-token-client")
	@ResponseBody
	public DataResponse saveTokenClient(@RequestBody TokenObject token) {
		System.out.println("Token = " + token);
		return new DataResponse("OK");
	}

	@PostMapping("/receive-web-hook")
	@ResponseBody
	public DataResponse receiveWebHook(List<Map<String,Object>> payload) {
		log.info("=========================WEB HOOK========================");
		log.info(payload.toString());
		return new DataResponse("OK");
	}

	@RequestMapping(path = "/file-to-base64", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseBody
	public DataResponse parseFileToBase64(@RequestBody MultipartFile file) throws IOException {
		byte[] image = Base64.encodeBase64(file.getBytes(), false);
		String encodedString = new String(image);
		return new DataResponse(encodedString);
	}

	@RequestMapping(path = "/uploads3", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseBody
	public DataResponse uploadS3(@RequestBody MultipartFile multipartFile) throws IOException {
		if (multipartFile == null) {
			return new DataResponse<>("Can't upload null file");
		}
		String dir = "/temp/cv/";
		String pattern = "yyyy_MM_dd-HH_mm_ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());
		String fileName = Objects.requireNonNull(multipartFile.getOriginalFilename()).concat(date).concat(".pdf");
		try {
			Path path = Paths.get(dir);
			Files.createDirectories(path);
			System.out.println("Directory is created!");
			//Files.createDirectory(path);

		} catch (IOException e) {
			System.err.println("Failed to create directory!" + e.getMessage());
		}

		// System.out.println(date);
		//return keyValue.concat(date).concat(getSuffixByFileType(fileCategory));
		//		String filePath = dir.concat(fileName);
		//		File tempLocalFile = new File(filePath);
		//		multipartFile.transferTo(tempLocalFile);
		String objectUrl = amazoneBucketService.uploadObjectFromInputStream(multipartFile, FileCategory.PDF_CV, true);

		return new DataResponse(objectUrl);
	}

	@GetMapping("/list-object")
	@ResponseBody
	public DataResponse listObject() {
		String bucketName = "haovu429";
		return new DataResponse(amazoneBucketService.listBucketObjects(bucketName));
	}
}
