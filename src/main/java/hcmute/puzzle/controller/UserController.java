package hcmute.puzzle.controller;

import hcmute.puzzle.configuration.security.CustomUserDetails;
import hcmute.puzzle.exception.*;
import hcmute.puzzle.filter.JwtAuthenticationFilter;
import hcmute.puzzle.infrastructure.converter.Converter;
import hcmute.puzzle.infrastructure.dtos.news.UpdateUserDto;
import hcmute.puzzle.infrastructure.dtos.news.UserPostDto;
import hcmute.puzzle.infrastructure.dtos.olds.CandidateDto;
import hcmute.puzzle.infrastructure.dtos.olds.EmployerDto;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.dtos.request.BlogPostRequest;
import hcmute.puzzle.infrastructure.dtos.request.BlogPostUpdateRequest;
import hcmute.puzzle.infrastructure.entities.BlogPostEntity;
import hcmute.puzzle.infrastructure.entities.InvoiceEntity;
import hcmute.puzzle.infrastructure.entities.UserEntity;
import hcmute.puzzle.infrastructure.mappers.UserMapper;
import hcmute.puzzle.infrastructure.models.enums.FileCategory;
import hcmute.puzzle.infrastructure.models.response.DataResponse;
import hcmute.puzzle.infrastructure.repository.BlogPostRepository;
import hcmute.puzzle.infrastructure.repository.UserRepository;
import hcmute.puzzle.services.*;
import hcmute.puzzle.utils.Constant;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Optional;


@RestController
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = {Constant.LOCAL_URL, Constant.ONLINE_URL})
@RequestMapping(path = "/user")
public class UserController {

  @Autowired
  UserService userService;

  @Autowired
  JwtAuthenticationFilter jwtAuthenticationFilter;

  @Autowired
  Converter converter;

  @Autowired
  FilesStorageService storageService;

  @Autowired
  UserRepository userRepository;

  @Autowired
  EmployerService employerService;

  @Autowired
  CandidateService candidateService;

  @Autowired
  JobPostService jobPostService;

  @Autowired
  InvoiceService invoiceService;

  @Autowired
  SubscribeService subscribeService;

  @Autowired
  BlogPostService blogPostService;

  @Autowired
  BlogPostRepository blogPostRepository;

  @GetMapping("/")
  public ResponseObject getAll() {
    return userService.getAll();
  }

  @GetMapping("/profile")
  public ResponseObject getProfileAccount() {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    UserPostDto userPostDTO = UserMapper.INSTANCE.userToUserPostDto(userDetails.getUser());

    return new ResponseObject(200, "Profile info", userPostDTO);
  }

  @DeleteMapping("/{id}")
  public ResponseObject deleteAccount(@PathVariable Long id) {
    return userService.delete(id);
  }

  @PutMapping("")
  public DataResponse updateAccount(@RequestBody UpdateUserDto user) {

    // Optional<UserEntity>
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return userService.update(userDetails.getUser().getId(), user);
  }

  //  @GetMapping("/test")
  //  public String update() {
  //    return "OK";
  //  }

  @PostMapping("/upload-avatar")
  public DataResponse uploadAvatar(@RequestParam("file") MultipartFile file) throws NotFoundException {
    CustomUserDetails userDetails =
            (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return userService.updateAvatarForUser(userDetails.getUser(), file, FileCategory.IMAGE_AVATAR);
  }

  @DeleteMapping("/delete-avatar")
  public ResponseObject deleteAvatar()
          throws ServerException, NotFoundException {
    CustomUserDetails userDetails =
            (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    String avatarUrl = userDetails.getUser().getAvatar();

    boolean result =
            storageService.deleteFile(
                    avatarUrl, FileCategory.IMAGE_AVATAR, userDetails.getUser(), true);

    if (!result) {
      throw new ServerException(ErrorDefine.ServerError.STORAGE_ERROR);
      // throw new CustomException("Delete image failure, response isn't ok");
    }

    userDetails.getUser().setAvatar(null);

    userRepository.save(userDetails.getUser());

    return new ResponseObject("Delete image success");
  }

  @PostMapping("/register-employer")
  ResponseObject registerEmployer(
      @RequestBody @Validated EmployerDto employer,
      BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new CustomException(bindingResult.getFieldError().toString());
    }

    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (userDetails.getUser().getCandidateEntity() != null) {
      throw new CustomException("This account is Candidate!");
    }

    if (userDetails.getUser().getEmployerEntity() != null) {
      throw new CustomException("Info employer for this account was created!");
    }

    employer.setUserId(userDetails.getUser().getId());

    Optional<EmployerDto> employerDTO = employerService.save(employer);
    if (employerDTO.isPresent()) {
      return new ResponseObject(
          HttpStatus.OK.value(), "Create employer successfully", employerDTO.get());
    } else {
      throw new CustomException("Register employer failed");
    }
  }

  @PostMapping("/register-candidate")
  ResponseObject registerCandidate(
      @RequestBody @Validated CandidateDto candidate,
      BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new RuntimeException(Objects.requireNonNull(bindingResult.getFieldError()).toString());
    }

    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (userDetails.getUser().getEmployerEntity() != null) {
      throw new CustomException("This account is Employer!");
    }

    if (userDetails.getUser().getCandidateEntity() != null) {
      throw new CustomException("Info candidate for this account was created!");
    }
    candidate.setUserId(userDetails.getUser().getId());

    if (!(candidate.getEmailContact() != null
        && !candidate.getEmailContact().isEmpty()
        && !candidate.getEmailContact().isBlank())) {
      throw new CustomException("Email contact invalid");
    }

    Optional<CandidateDto> candidateDTO = candidateService.save(candidate);
    if (candidateDTO.isPresent()) {
      return new ResponseObject(
          HttpStatus.OK.value(), "Create candidate successfully", candidateDTO.get());
    } else {
      throw new RuntimeException("Add candidate failed");
    }

    //        return new ResponseObject(
    //                HttpStatus.OK.value(), "Create candidate successfully", new CandidateDto());
  }

  @GetMapping("/get-viewed-job-post-amount")
  DataResponse getAmountApplicationToEmployer() {

    CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (customUserDetails.getUser() == null) {
      throw new CustomException("Not found account");
    }

    return jobPostService.getViewedJobPostAmountByUserId(customUserDetails.getUser().getId());
  }

  // log history viewed Job Post
  @GetMapping("/view-job-post/{jobPostId}")
  DataResponse getViewJobPost(
      HttpServletRequest request, @PathVariable(value = "jobPostId") long jobPostId) {

    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromRequest(request);

    if (linkUser.isEmpty()) {
      throw new CustomException("Not found account");
    }

    return jobPostService.viewJobPost(linkUser.get().getId(), jobPostId);
  }

  @GetMapping("/get-invoice")
  public DataResponse getInvoiceForUser() {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return invoiceService.getInvoiceByEmailUser(userDetails.getUser().getEmail());
  }

  @GetMapping("/get-one-invoice/{invoiceId}")
  public DataResponse getAllInvoice(@PathVariable(value = "invoiceId") long invoiceId) {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    InvoiceEntity invoice = invoiceService.getOneInvoice(invoiceId);
    if (!userDetails.getUser().getEmail().equals(invoice.getEmail())) {
      throw new CustomException("You don't have rights for this invoice");
    }
    return new DataResponse(converter.toDTO(invoice));
  }

  @GetMapping("/get-subscribed-package")
  public DataResponse getPackageSubscribe() {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    return subscribeService.getAllSubscriptionsByUserId(userDetails.getUser().getId());
  }

  @RequestMapping(
          path = "/create-blog-post",
          method = RequestMethod.POST,
          consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public DataResponse createBlogPost(
          @ParameterObject @ModelAttribute BlogPostRequest blogPostRequest,
          @RequestPart(required = false) MultipartFile thumbnail) {
    if (thumbnail != null && !thumbnail.isEmpty()) {
      blogPostRequest.setThumbnail(thumbnail);
    }
    return blogPostService.createBlogPost(blogPostRequest);
  }

  @RequestMapping(
          path = "/update-blog-post/{blogPostId}",
          method = RequestMethod.PUT,
          consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public DataResponse updateBlogPost(
          @ParameterObject @ModelAttribute BlogPostUpdateRequest blogPostUpdateRequest,
          @RequestPart(required = false) MultipartFile thumbnail,
          @PathVariable long blogPostId) {
    //BlogPostDto blogPostDTO = mapper.map(blogPostRequest, BlogPostDto.class);
    //blogPostDTO.setUserId(((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getId());

    //    UserEntity currentUser =
    //        ((CustomUserDetails)
    // SecurityContextHolder.getContext().getAuthentication().getPrincipal())
    //            .getUser();
    if (thumbnail != null && !thumbnail.isEmpty()) {
      blogPostUpdateRequest.setThumbnail(thumbnail);
    }
    return blogPostService.update(blogPostUpdateRequest, blogPostId);
  }

  @DeleteMapping("/delete-blog-post/{blogPostId}")
  public DataResponse deleteBlogPost(@PathVariable long blogPostId) {
    CustomUserDetails userDetails =
            (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<BlogPostEntity> blogPost = blogPostRepository.findById(blogPostId);
    if (blogPost.get().getAuthor().getId() != userDetails.getUser().getId()) {
      throw new CustomException("You don't have rights for this blog post");
    }
    return blogPostService.delete(blogPostId);
  }

  @PostMapping("/upload-blog-image")
  public DataResponse<String> uploadBlogImage(
          @RequestParam("file") MultipartFile file, Authentication authentication)
          throws NotFoundException {
    String fileName =
            storageService
                    .uploadFileWithFileTypeReturnUrl(
                            file.getOriginalFilename(), file, FileCategory.IMAGE_BLOG, true)
                    .orElseThrow(() -> new FileStorageException("UPLOAD_FILE_FAILURE"));
    return new DataResponse(fileName);
  }
}
