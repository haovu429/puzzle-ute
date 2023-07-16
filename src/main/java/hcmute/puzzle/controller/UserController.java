package hcmute.puzzle.controller;

import hcmute.puzzle.configuration.security.CustomUserDetails;
import hcmute.puzzle.exception.*;
import hcmute.puzzle.filter.JwtAuthenticationFilter;
import hcmute.puzzle.infrastructure.converter.Converter;
import hcmute.puzzle.infrastructure.dtos.news.*;
import hcmute.puzzle.infrastructure.dtos.olds.*;
import hcmute.puzzle.infrastructure.dtos.request.*;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
import hcmute.puzzle.infrastructure.entities.BlogPost;
import hcmute.puzzle.infrastructure.entities.User;
import hcmute.puzzle.infrastructure.mappers.UserMapper;
import hcmute.puzzle.infrastructure.models.enums.FileCategory;
import hcmute.puzzle.infrastructure.models.enums.FileType;
import hcmute.puzzle.infrastructure.repository.BlogPostRepository;
import hcmute.puzzle.infrastructure.repository.UserRepository;
import hcmute.puzzle.services.*;
import hcmute.puzzle.services.impl.*;
import hcmute.puzzle.utils.Constant;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
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
  SubscriptionService subscriptionService;

  @Autowired
  BlogPostService blogPostService;

  @Autowired
  BlogPostRepository blogPostRepository;

  @Autowired
  CommentService commentService;

  //  @GetMapping("/")
  //  public DataResponse getAll(Pageable pageable) {
  //    List<UserPostDto> userPostDtos = userService.getAll();
  //    return new DataResponse(userPostDtos);
  //  }

  @GetMapping("/profile")
  public DataResponse<UserPostDto> getProfileAccount() {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                             .getAuthentication()
                                                                             .getPrincipal();
    UserPostDto userPostDTO = UserMapper.INSTANCE.userToUserPostDto(userDetails.getUser());
    return new DataResponse<>(userPostDTO);
  }

  @DeleteMapping("")
  public DataResponse<String> deleteAccount() {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                             .getAuthentication()
                                                                             .getPrincipal();
    userService.delete(userDetails.getUser().getId());
    return new DataResponse<>("Success");
  }

  @PutMapping("")
  public DataResponse<UserPostDto> updateAccount(@RequestBody UpdateUserDto user) {

    // Optional<UserEntity>
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    UserPostDto userPostDto = userService.update(userDetails.getUser().getId(), user);
    return new DataResponse<>(userPostDto);
  }

  //  @GetMapping("/test")
  //  public String update() {
  //    return "OK";
  //  }

  @PostMapping("/upload-avatar")
  public DataResponse<UserPostDto> uploadAvatar(@RequestParam("file") MultipartFile file) throws NotFoundException {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                             .getAuthentication()
                                                                             .getPrincipal();
    UserPostDto userPostDto = userService.updateAvatarForUser(userDetails.getUser(), file, FileCategory.IMAGE_AVATAR);
    return new DataResponse<>(userPostDto);
  }

  @DeleteMapping("/delete-avatar")
  public DataResponse<String> deleteAvatar() throws ServerException, NotFoundException {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                             .getAuthentication()
                                                                             .getPrincipal();
    String avatarUrl = userDetails.getUser().getAvatar();
    boolean result = storageService.deleteFile(avatarUrl, FileCategory.IMAGE_AVATAR, true);

    if (!result) {
      throw new ServerException(ErrorDefine.ServerError.STORAGE_ERROR);
      // throw new CustomException("Delete image failure, response isn't ok");
    }
    userDetails.getUser().setAvatar(null);
    userRepository.save(userDetails.getUser());
    return new DataResponse<>("Delete image success");
  }

  @PostMapping("/register-employer")
  DataResponse<EmployerDto> registerEmployer(@RequestBody @Validated EmployerDto employer,
          BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new CustomException(Objects.requireNonNull(bindingResult.getFieldError()).toString());
    }

    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                             .getAuthentication()
                                                                             .getPrincipal();
    if (userDetails.getUser().getCandidate() != null) {
      throw new CustomException("This account is Candidate!");
    }

    if (userDetails.getUser().getEmployer() != null) {
      throw new CustomException("Info employer for this account was created!");
    }
    employer.setUserId(userDetails.getUser().getId());

    EmployerDto employerDto = employerService.save(employer);
    return new DataResponse<>(employerDto);
  }

  @PostMapping("/register-candidate")
  DataResponse<CandidateDto> registerCandidate(@RequestBody @Validated CandidateDto candidate,
          BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new RuntimeException(Objects.requireNonNull(bindingResult.getFieldError()).toString());
    }

    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                             .getAuthentication()
                                                                             .getPrincipal();

    if (userDetails.getUser().getEmployer() != null) {
      throw new CustomException("This account is Employer!");
    }

    if (userDetails.getUser().getCandidate() != null) {
      throw new CustomException("Info candidate for this account was created!");
    }
    candidate.setUserId(userDetails.getUser().getId());

    if (!(candidate.getEmailContact() != null && !candidate.getEmailContact().isEmpty() && !candidate.getEmailContact()
                                                                                                     .isBlank())) {
      throw new CustomException("Email contact invalid");
    }

    CandidateDto candidateDto = candidateService.save(candidate);
    return new DataResponse<>(candidateDto);


    //        return new DataResponse(
    //                HttpStatus.OK.value(), "Create candidate successfully", new CandidateDto());
  }

  @GetMapping("/get-viewed-job-post-amount")
  DataResponse<Long> getAmountApplicationToEmployer() {

    CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                                   .getAuthentication()
                                                                                   .getPrincipal();

    if (customUserDetails.getUser() == null) {
      throw new CustomException("Not found account");
    }
    long amount = jobPostService.getViewedJobPostAmountByUserId(customUserDetails.getUser().getId());
    return new DataResponse<>(amount);
  }

  // log history viewed Job Post
//  @GetMapping("/view-job-post/{jobPostId}")
//  DataResponse<String> getViewJobPost(HttpServletRequest request, @PathVariable(value = "jobPostId") long jobPostId) {
//
//    Optional<User> linkUser = jwtAuthenticationFilter.getUserEntityFromRequest(request);
//
//    if (linkUser.isEmpty()) {
//      throw new CustomException("Not found account");
//    }
//    jobPostService.viewJobPost(linkUser.get().getId(), jobPostId);
//    return new DataResponse<>("Success");
//  }

  @GetMapping("/get-invoice")
  public DataResponse<List<InvoiceDto>> getInvoiceForUser() {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                             .getAuthentication()
                                                                             .getPrincipal();
    List<InvoiceDto> invoiceDtos = invoiceService.getInvoiceByEmailUser(userDetails.getUser().getEmail());
    return new DataResponse<>(invoiceDtos);
  }

  @GetMapping("/get-one-invoice/{invoiceId}")
  public DataResponse<InvoiceDto> getAllInvoice(@PathVariable(value = "invoiceId") long invoiceId) {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                             .getAuthentication()
                                                                             .getPrincipal();
    InvoiceDto invoiceDto = invoiceService.getOneInvoice(invoiceId);
    if (!userDetails.getUser().getEmail().equals(invoiceDto.getEmail())) {
      throw new CustomException("You don't have rights for this invoice");
    }
    return new DataResponse<>(invoiceDto);
  }

  @GetMapping("/get-subscribed-package")
  public DataResponse getPackageSubscribe() {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                             .getAuthentication()
                                                                             .getPrincipal();

    return subscriptionService.getAllSubscriptionsByUserId(userDetails.getUser().getId());
  }

  @RequestMapping(
          path = "/my-blog-post",
          method = RequestMethod.GET
  )
  public DataResponse<List<BlogPostDto>> getMyBlogPost() {
    CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    long userId = customUserDetails.getUser().getId();
    return new DataResponse<>(blogPostService.getBlogPostByUser(userId));
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
    Optional<BlogPost> blogPost = blogPostRepository.findById(blogPostId);
    if (blogPost.get().getAuthor().getId() != userDetails.getUser().getId()) {
      throw new CustomException("You don't have rights for this blog post");
    }
    return blogPostService.delete(blogPostId);
  }

  @PostMapping("/upload-blog-image")
  public DataResponse<String> uploadBlogImage(@RequestParam("file") MultipartFile file) throws NotFoundException {
    String fileName = storageService.uploadFileWithFileTypeReturnUrl(file.getOriginalFilename(), file, FileType.IMAGE, FileCategory.IMAGE_BLOG, true)
                                    .orElseThrow(() -> new FileStorageException("UPLOAD_FILE_FAILURE"));
    return new DataResponse<>(fileName);
  }

  @PostMapping(path = "/comment/add")
  public DataResponse<CommentDto> addComment(@Valid @RequestBody CreateCommentRequest createCommentRequest,
          @RequestParam Long blogPostId) {
    return new DataResponse<>(commentService.addComment(createCommentRequest, blogPostId));
  }

  @PutMapping(path = "/comment/update")
  public DataResponse<CommentDto> updateComment(@RequestParam Long commentId,
          @RequestBody UpdateCommentRequest updateCommentRequest) {
    try {
      CommentDto commentDto = commentService.updateComment(commentId, updateCommentRequest);
      return new DataResponse<>(commentDto);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw e;
    }
  }

  @DeleteMapping(path = "/comment/delete")
  public DataResponse<String> deleteComment(@RequestParam Long commentId) {
    try {
      commentService.deleteComment(commentId);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw e;
    }
    return new DataResponse<>("Success");
  }



  @RequestMapping(path = "/sub-comment/add", method = RequestMethod.POST)
  public DataResponse<SubCommentDto> addSubComment(@Valid @RequestBody CreateSubCommentRequest createSubCommentRequest,
          @RequestParam long commentId) {
    return new DataResponse<>(commentService.addSubComment(createSubCommentRequest, commentId));
  }

  @PutMapping(path = "/sub-comment/update")
  public DataResponse<SubCommentDto> updateSubComment(@RequestParam Long subCommentId,
          @RequestBody UpdateSubCommentRequest updateSubCommentRequest) {
    try {
      SubCommentDto subCommentDto = commentService.updateSubComment(subCommentId, updateSubCommentRequest);
      return new DataResponse<>(subCommentDto);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw e;
    }
  }

  @DeleteMapping(path = "/sub-comment/delete")
  public DataResponse<String> deleteSubComment(@RequestParam Long subCommentId) {
    try {
      commentService.deleteSubComment(subCommentId);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw e;
    }
    return new DataResponse<>("Success");
  }

  private Pageable getPageable(Integer page, Integer size) {
    Pageable pageable = Pageable.unpaged();
    if (page != null && size != null) {
      pageable = Pageable.ofSize(size).withPage(page);
    }
    return pageable;
  }

  @GetMapping(path = "/cv-online/my-cv")
  public DataResponse<List<JsonDataDto>> getMyCvOnline() {
    return new DataResponse<>(userService.getMyCvOnlineJsonData());
  }

  @GetMapping(path = "/cv-online/{jsonDataId}")
  public DataResponse<JsonDataDto> getCvOnlineById(@PathVariable long jsonDataId) {
    return new DataResponse<>(userService.getJsonDataTypeCvById(jsonDataId));
  }

  @PostMapping(path = "/cv-online")
  public DataResponse<JsonDataDto> addCvOnline(@RequestBody CreateJsonDataTypeCvRequest createJsonDataTypeCvRequest) {
    return new DataResponse<>(userService.addCvOnlineJsonData(createJsonDataTypeCvRequest));
  }

  @PutMapping(path = "/cv-online/{jsonDataId}")
  public DataResponse<JsonDataDto> updateCvOnline(@PathVariable long jsonDataId, @RequestBody UpdateJsonDataTypeCvRequest updateJsonDataTypeCvRequest) {
    return new DataResponse<>(userService.updateCvOnlineJsonData(jsonDataId, updateJsonDataTypeCvRequest));
  }

  @DeleteMapping(path = "/cv-online/{jsonDataId}")
  public DataResponse<String> deleteCvOnline(@PathVariable long jsonDataId) {
    userService.deleteCvOnlineJsonData(jsonDataId);
    return new DataResponse<>("Success");
  }

}
