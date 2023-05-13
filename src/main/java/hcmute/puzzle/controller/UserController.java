package hcmute.puzzle.controller;

import hcmute.puzzle.infrastructure.converter.Converter;

import hcmute.puzzle.infrastructure.dtos.news.UpdateUserDto;
import hcmute.puzzle.infrastructure.dtos.news.UserPostDto;
import hcmute.puzzle.infrastructure.dtos.olds.BlogPostDto;
import hcmute.puzzle.infrastructure.dtos.olds.CandidateDto;
import hcmute.puzzle.infrastructure.dtos.olds.EmployerDto;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.entities.*;
import hcmute.puzzle.exception.*;
import hcmute.puzzle.filter.JwtAuthenticationFilter;
import hcmute.puzzle.infrastructure.mappers.UserMapper;
import hcmute.puzzle.infrastructure.models.enums.FileCategory;
import hcmute.puzzle.infrastructure.models.payload.request.blog_post.CreateBlogPostPayload;
import hcmute.puzzle.infrastructure.models.payload.request.user.UpdateUserPayload;
import hcmute.puzzle.infrastructure.repository.BlogPostRepository;
import hcmute.puzzle.infrastructure.repository.UserRepository;
import hcmute.puzzle.infrastructure.models.response.DataResponse;
import hcmute.puzzle.configuration.security.CustomUserDetails;
import hcmute.puzzle.services.*;
import hcmute.puzzle.utils.Constant;

import java.util.*;
import javax.servlet.http.HttpServletRequest;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = {Constant.LOCAL_URL, Constant.ONLINE_URL})
public class UserController {

  @Autowired UserService userService;

  @Autowired JwtAuthenticationFilter jwtAuthenticationFilter;

  @Autowired Converter converter;

  @Autowired FilesStorageService storageService;

  @Autowired UserRepository userRepository;

  @Autowired EmployerService employerService;

  @Autowired CandidateService candidateService;

  @Autowired JobPostService jobPostService;

  @Autowired InvoiceService invoiceService;

  @Autowired SubscribeService subscribeService;

  @Autowired BlogPostService blogPostService;

  @Autowired BlogPostRepository blogPostRepository;

  @GetMapping("/user")
  public ResponseObject getAll() {
    return userService.getAll();
  }

  @GetMapping("/user/profile")
  public ResponseObject getProfileAccount() {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    UserPostDto userPostDTO = UserMapper.INSTANCE.userToUserPostDto(userDetails.getUser());
            //converter.toDTO(userDetails.getUser());


    return new ResponseObject(200, "Profile info", userPostDTO);
  }

  @DeleteMapping("/user/{id}")
  public ResponseObject deleteAccount(@PathVariable Long id) {
    return userService.delete(id);
  }

  @PutMapping("/user")
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
  public DataResponse uploadAvatar(
      @RequestParam("file") MultipartFile file, Authentication authentication)
      throws NotFoundException {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    return userService.updateAvatarForUser(userDetails.getUser(), file, FileCategory.IMAGE_AVATAR);
  }

  @GetMapping("/delete-avatar")
  public ResponseObject deleteAvatar(Authentication authentication)
      throws ServerException, NotFoundException {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

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

  @PostMapping("/user/register-employer")
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
      throw new CustomException("Add employer failed");
    }

    //    return new ResponseObject(
    //        HttpStatus.OK.value(), "Create employer successfully", new EmployerDto());
  }

  @PostMapping("/user/register-candidate")
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

  @GetMapping("/user/get-viewed-job-post-amount")
  DataResponse getAmountApplicationToEmployer() {

    CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


    if (customUserDetails.getUser() == null) {
      throw new CustomException("Not found account");
    }

    return jobPostService.getViewedJobPostAmountByUserId(customUserDetails.getUser().getId());
  }

  // log history viewed Job Post
  @GetMapping("/user/view-job-post/{jobPostId}")
  DataResponse getViewJobPost(
      HttpServletRequest request, @PathVariable(value = "jobPostId") long jobPostId) {

    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromRequest(request);

    if (linkUser.isEmpty()) {
      throw new CustomException("Not found account");
    }

    return jobPostService.viewJobPost(linkUser.get().getId(), jobPostId);
  }

  @GetMapping("/user/get-invoice")
  public DataResponse getInvoiceForUser() {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return invoiceService.getInvoiceByEmailUser(userDetails.getUser().getEmail());
  }

  @GetMapping("/user/get-one-invoice/{invoiceId}")
  public DataResponse getAllInvoice(@PathVariable(value = "invoiceId") long invoiceId) {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    InvoiceEntity invoice = invoiceService.getOneInvoice(invoiceId);
    if (!userDetails.getUser().getEmail().equals(invoice.getEmail())) {
      throw new CustomException("You don't have rights for this invoice");
    }
    return new DataResponse(converter.toDTO(invoice));
  }

  @GetMapping("/user/get-subscribed-package")
  public DataResponse getPackageSubscribe() {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    return subscribeService.getAllSubscriptionsByUserId(userDetails.getUser().getId());
  }

  @PostMapping("/user/create-blog-post")
  public DataResponse createBlogPost(@RequestBody CreateBlogPostPayload createBlogPostPayload) {
    ModelMapper mapper = new ModelMapper();
    BlogPostDto blogPostDTO = mapper.map(createBlogPostPayload, BlogPostDto.class);
    blogPostDTO.setCreateTime(new Date());
    blogPostDTO.setUserId(((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getId());
    return blogPostService.createBlogPost(blogPostDTO);
  }

  @PutMapping("/user/update-blog-post/{blogPostId}")
  public DataResponse updateBlogPost(
      @RequestBody CreateBlogPostPayload createBlogPostPayload,
      @PathVariable long blogPostId) {
      ModelMapper mapper = new ModelMapper();
    BlogPostDto blogPostDTO = mapper.map(createBlogPostPayload, BlogPostDto.class);
    blogPostDTO.setUserId(((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getId());

    //    UserEntity currentUser =
    //        ((CustomUserDetails)
    // SecurityContextHolder.getContext().getAuthentication().getPrincipal())
    //            .getUser();
    return blogPostService.update(blogPostDTO, blogPostId);
  }

  @DeleteMapping("/user/delete-blog-post/{blogPostId}")
  public DataResponse deleteBlogPost(@PathVariable long blogPostId) {
    CustomUserDetails userDetails =
            (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<BlogPostEntity> blogPost = blogPostRepository.findById(blogPostId);
    if (blogPost.get().getAuthor().getId() != userDetails.getUser().getId()) {
      throw new CustomException("You don't have rights for this blog post");
    }
    return blogPostService.delete(blogPostId);
  }

  @PostMapping("/user/upload-blog-image")
  public DataResponse uploadBlogImage(
      @RequestParam("file") MultipartFile file, Authentication authentication)
      throws NotFoundException {
    FileEntity fileEntity =
        storageService
            .uploadFileWithFileTypeReturnUrl(
                file.getOriginalFilename(), file, FileCategory.IMAGE_BLOG)
            .orElseThrow(() -> new FileStorageException("UPLOAD_FILE_FAILURE"));
    String fileName = fileEntity.getUrl();
    return new DataResponse(fileName);
  }
}
