package hcmute.puzzle.services.impl;

import hcmute.puzzle.configuration.security.CustomUserDetails;
import hcmute.puzzle.exception.*;
import hcmute.puzzle.infrastructure.converter.Converter;
import hcmute.puzzle.infrastructure.dtos.olds.BlogPostDto;
import hcmute.puzzle.infrastructure.dtos.request.BlogPostRequest;
import hcmute.puzzle.infrastructure.dtos.request.BlogPostUpdateRequest;
import hcmute.puzzle.infrastructure.entities.BlogPostEntity;
import hcmute.puzzle.infrastructure.entities.CategoryEntity;
import hcmute.puzzle.infrastructure.entities.FileEntity;
import hcmute.puzzle.infrastructure.entities.UserEntity;
import hcmute.puzzle.infrastructure.mappers.BlogPostMapper;
import hcmute.puzzle.infrastructure.models.enums.FileCategory;
import hcmute.puzzle.infrastructure.models.response.DataResponse;
import hcmute.puzzle.infrastructure.repository.BlogPostRepository;
import hcmute.puzzle.infrastructure.repository.CategoryRepository;
import hcmute.puzzle.infrastructure.repository.FileRepository;
import hcmute.puzzle.services.BlogPostService;
import hcmute.puzzle.services.FilesStorageService;
import hcmute.puzzle.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class BlogPostServiceImpl implements BlogPostService {

  @Autowired
  Converter converter;

  @Autowired
  BlogPostRepository blogPostRepository;

  @Autowired
  FileRepository fileRepository;

  @Autowired
  FilesStorageService filesStorageService;

  @Autowired
  CategoryRepository categoryRepository;

  @Autowired
  BlogPostMapper blogPostMapper;

  public DataResponse createBlogPost(BlogPostRequest blogPostRequest) {

    CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    UserEntity author = customUserDetails.getUser();

    CategoryEntity category = categoryRepository.findById(blogPostRequest.getCategoryId())
            .orElseThrow(() -> new NotFoundDataException("NOT_FOUND_CATEGORY"));

    BlogPostEntity blogPost = BlogPostEntity.builder()
            .title(blogPostRequest.getTitle())
            .body(blogPostRequest.getBody())
            .tags(blogPostRequest.getTags())
            .category(category)
            .author(author)
            .build();
    blogPost = blogPostRepository.save(blogPost);
    //BlogPostMapper.INSTANCE.blogPostDtoToBlogPost(dto);

    String publicName = String.format("%s_%s", blogPost.getId(), Constant.SUFFIX_BLOG_POST_THUMBNAIL);
    String imageUrl = filesStorageService.uploadFileWithFileTypeReturnUrl(publicName,
                    blogPostRequest.getThumbnail(), FileCategory.THUMBNAIL_BLOGPOST, true)
            .orElseThrow(() -> new FileStorageException("UPLOAD_THUMBNAIL_FAIL"));
    blogPost.setThumbnail(imageUrl);
    // blogPostRepository.save(blogPost);

    blogPost = blogPostRepository.save(blogPost);
    // process file (update blog id for file saved image)
    List<String> imageUrls = null;
    List<FileEntity> savedImages = null;

    imageUrls = detectedImageSrcList(blogPost.getBody());

    if (imageUrls != null && !imageUrls.isEmpty()) {
      savedImages = fileRepository.findAllByUrlInAndDeletedIs(imageUrls, false);
    }

    if (savedImages != null && !savedImages.isEmpty()) {
      BlogPostEntity finalBlogPost = blogPost;
      savedImages.forEach(image -> image.setObjectId(finalBlogPost.getId()));
      fileRepository.saveAll(savedImages);
    }
    BlogPostDto blogPostDto = blogPostMapper.blogPostToBlogPostDto(blogPost);

    return new DataResponse(blogPostDto);
  }

  private void resolveBlogPostDtoWithRightEditOfCommentSubComment(BlogPostDto blogPostDto, long currentUserId) {
    blogPostDto.getComments().forEach(
            commentDto -> {
              commentDto.getSubComments()
                      .forEach(subCommentDto -> {
                                if (Objects.equals(subCommentDto.getUserId(), currentUserId)) {
                                  subCommentDto.setCanEdit(true);
                                } else {
                                  subCommentDto.setCanEdit(false);
                                }
                              }
                      );
              if (Objects.equals(commentDto.getUserId(), currentUserId)) {
                commentDto.setCanEdit(true);
              } else {
                commentDto.setCanEdit(false);
              }
            }
    );
//    List<CommentDto> commentDtos = blogPostDto.getComments();
//    if (commentDtos != null && !commentDtos.isEmpty()) {
//      commentDtos.forEach(commentDto -> {
//                commentDto.getSubComments()
//                        .forEach(subCommentDto -> {
//                                  if (Objects.equals(subCommentDto.getUserId(), currentUserId)) {
//                                    subCommentDto.setCanEdit(true);
//                                  } else {
//                                    subCommentDto.setCanEdit(false);
//                                  }
//                                }
//                        );
//                if (Objects.equals(commentDto.getUserId(), currentUserId)) {
//                  commentDto.setCanEdit(true);
//                } else {
//                  commentDto.setCanEdit(false);
//                }
//              }
//      );
//    }
//    blogPostDto.setComments(commentDtos);
  }

  @Override
  public DataResponse update(BlogPostUpdateRequest blogPostUpdateRequest, long id) {
    BlogPostEntity blogPost = blogPostRepository.findById(id).orElseThrow(
            () -> new CustomException("You don't have rights for this blog post")
    );

    if (Objects.nonNull(blogPostUpdateRequest.getCategoryId())) {
      CategoryEntity category = categoryRepository.findById(blogPostUpdateRequest.getCategoryId())
              .orElseThrow(() -> new NotFoundDataException("NOT_FOUND_CATEGORY"));
      blogPost.setCategory(category);
    }

    CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
    UserEntity userEntity = customUserDetails.getUser();
    if (userEntity.getId() != blogPost.getAuthor().getId()) {
      throw new UnauthorizedException("UNAUTHORIZED FOR THIS BLOG POST");
    }
    if (blogPost.getBody() != null && blogPostUpdateRequest.getBody() != null) {
      try {
        List<String> oldSrcs = detectedImageSrcList(blogPost.getBody());
        List<String> newSrcs = detectedImageSrcList(blogPostUpdateRequest.getBody());

        // update blog post id for new saved file in db
        List<String> addedSrcs = getUrlOfFirstListWhichSecondListNotContain(newSrcs, oldSrcs);
        updateBlogPostIdForSavedFile(addedSrcs, blogPost.getId());

        // release file don't use
        List<String> urlToDelete = getUrlOfFirstListWhichSecondListNotContain(oldSrcs, newSrcs);
        deleteMultiFileByUrlList(urlToDelete);

      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    }
    blogPostMapper.updateBlogPostFromBlogPostRequestWithoutThumbnail(blogPostUpdateRequest, blogPost);
    // blogPost.updateFromDTO(dto);
    blogPostRepository.save(blogPost);

    if (blogPostUpdateRequest.getThumbnail() != null) {
      String publicName = String.format("%s_%s", blogPost.getId(), Constant.SUFFIX_BLOG_POST_THUMBNAIL);
      String imageUrl = filesStorageService.uploadFileWithFileTypeReturnUrl(publicName,
                      blogPostUpdateRequest.getThumbnail(), FileCategory.THUMBNAIL_BLOGPOST, true)
              .orElseThrow(() -> new FileStorageException("UPLOAD_THUMBNAIL_FAIL"));
      blogPost.setThumbnail(imageUrl);
    }
    BlogPostDto blogPostDto = blogPostMapper.blogPostToBlogPostDto(blogPost);
    resolveBlogPostDtoWithRightEditOfCommentSubComment(blogPostDto, userEntity.getId());
    return new DataResponse(blogPostDto);

  }

  private void updateBlogPostIdForSavedFile(List<String> fileSrcs, long blogPostId) {
    List<FileEntity> savedImages = null;

    //get current user
    UserEntity currentUser =
            ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                    .getUser();

    if (fileSrcs != null && !fileSrcs.isEmpty()) {
      savedImages = fileRepository.findAllByUrlInAndDeletedIs(fileSrcs, false);
    }
    if (savedImages != null && !savedImages.isEmpty()) {
      savedImages.forEach(
              image -> {
                image.setObjectId(blogPostId);
                image.setUpdatedBy(currentUser.getEmail());
                image.setUpdatedAt(new Date());
              });
      fileRepository.saveAll(savedImages);
    }
  }

  public void deleteMultiFileByUrlList(List<String> urls) {
    //get current user
    UserEntity currentUser =
            ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                    .getUser();

    List<String> publicIdsToDelete =
            fileRepository.findAllByUrlInAndDeletedIs(urls, false).stream()
                    .map(FileEntity::getCloudinaryPublicId).collect(Collectors.toList());
    if (!publicIdsToDelete.isEmpty())
      filesStorageService.deleteMultiFile(publicIdsToDelete, currentUser);
  }

  @Override
  public DataResponse delete(long id) {
    BlogPostEntity blogPost = blogPostRepository.findById(id).orElseThrow(() -> new NotFoundException("NOT_FOUND_BLOG_POST"));
    try {
      List<String> imageSrcs = detectedImageSrcList(blogPost.getBody());

      deleteMultiFileByUrlList(imageSrcs);
    } catch (Exception e) {
      //throw new FileStorageException("DO_NOT_DELETE_FILE_OF_BLOG_POST");
    }
    blogPostRepository.delete(blogPost);
    return new DataResponse("Success");
  }

  @Override
  public DataResponse getAll() {

    UserEntity currentUser;
    if (SecurityContextHolder.getContext().getAuthentication() != null
            && SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof CustomUserDetails) {
      currentUser = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    } else {
      currentUser = null;
    }

    List<BlogPostDto> dtos =
            blogPostRepository.findAll().stream()
                    .map(
                            entity -> {
                              BlogPostDto blogPostDto = blogPostMapper.blogPostToBlogPostDto(entity);// converter.toDTO(entity);
                              //blogPostDTO.setBody(null);
                              if (currentUser != null) {
                                resolveBlogPostDtoWithRightEditOfCommentSubComment(blogPostDto, currentUser.getId());
                              }
                              return blogPostDto;
                            })
                    .collect(Collectors.toList());
    return new DataResponse(dtos);
  }

  @Override
  public DataResponse getOneById(long id) {
    BlogPostDto dto =
            converter.toDTO(
                    blogPostRepository
                            .findById(id)
                            .orElseThrow(() -> new NotFoundException("NOT_FOUND_BLOG_POST")));
    return new DataResponse(dto);
  }

  public List<String> getUrlOfFirstListWhichSecondListNotContain(
          List<String> firstList, List<String> secondList) {
    // getDeletedBlogImageUrl
    return firstList.stream().filter(item -> !secondList.contains(item))
            .collect(Collectors.toList());
  }

  public List<String> detectedImageSrcList(String html) {
    if (html == null) {
      throw new NullPointerException("CONTENT DETECT IMAGE URL IS NULL");
    }
    List<String> imageSrcList = new ArrayList<>();
    final String regex = "<img.*?src=[\"|'](.*?)[\"|']";
    final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
    final Matcher matcher = pattern.matcher(html);
    while (matcher.find()) {
      // System.out.println("Full match: " + matcher.group(0));

      for (int i = 1; i <= matcher.groupCount(); i++) {
        imageSrcList.add(matcher.group(i));
        // System.out.println("Group " + i + ": " + matcher.group(i));
      }
    }

    return imageSrcList;
  }
}
