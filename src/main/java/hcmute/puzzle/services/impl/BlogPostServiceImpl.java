package hcmute.puzzle.services.impl;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.BlogPostDTO;
import hcmute.puzzle.entities.BlogPostEntity;
import hcmute.puzzle.entities.FileEntity;
import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.exception.NotFoundException;
import hcmute.puzzle.repository.BlogPostRepository;
import hcmute.puzzle.repository.FileRepository;
import hcmute.puzzle.response.DataResponse;
import hcmute.puzzle.security.CustomUserDetails;
import hcmute.puzzle.services.BlogPostService;
import hcmute.puzzle.services.FilesStorageService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class BlogPostServiceImpl implements BlogPostService {

  @Autowired private Converter converter;

  @Autowired private BlogPostRepository blogPostRepository;

  @Autowired private FileRepository fileRepository;

  @Autowired private FilesStorageService filesStorageService;

  @Override
  public DataResponse createBlogPost(BlogPostDTO dto) {
    BlogPostEntity blogPost = converter.toEntity(dto);
    blogPost = blogPostRepository.save(blogPost);
    // process file (update blog id for file saved image)
    List<String> imageUrls = null;
    List<FileEntity> savedImages = null;

    imageUrls = detectedImageSrcList(blogPost.getBody());

    if (imageUrls != null && !imageUrls.isEmpty()) {
      savedImages = fileRepository.findAllByUrlIdInAndDeletedIs(imageUrls, false);
    }

    if (savedImages != null && !savedImages.isEmpty()) {
      BlogPostEntity finalBlogPost = blogPost;
      savedImages.forEach(image -> image.setObjectId(finalBlogPost.getId()));
      fileRepository.saveAll(savedImages);
    }

    return new DataResponse(converter.toDTO(blogPost));
  }

  @Override
  public DataResponse update(BlogPostDTO dto, long id) {
    BlogPostEntity blogPost = blogPostRepository.getById(id);
    if (blogPost != null) {
      if (dto.getUserId() != blogPost.getCreatedBy().getId()) {
        throw new CustomException("You don't have rights for this blog post");
      }

      try {
        List<String> oldSrcs = detectedImageSrcList(blogPost.getBody());
        List<String> newSrcs = detectedImageSrcList(dto.getBody());

        // update blog post id for new saved file in db
        List<String> addedSrcs = getUrlOfFirstListWhichSecondListNotContain(newSrcs, oldSrcs);
        updateBlogPostIdForSavedFile(addedSrcs, blogPost.getId());

        // release file don't use
        List<String> urlToDelete = getUrlOfFirstListWhichSecondListNotContain(oldSrcs, newSrcs);
        deleteMultiFileByUrlList(urlToDelete);

        blogPost.updateFromDTO(dto);
        blogPostRepository.save(blogPost);

      } catch (Exception e) {
        e.printStackTrace();
      }

      return new DataResponse(converter.toDTO(blogPost));
    }
    throw new CustomException("Not found blog post");
  }

  private void updateBlogPostIdForSavedFile(List<String> fileSrcs, long blogPostId) {
    List<FileEntity> savedImages = null;

    //get current user
    UserEntity currentUser =
        ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getUser();

    if (fileSrcs != null && !fileSrcs.isEmpty()) {
      savedImages = fileRepository.findAllByUrlIdInAndDeletedIs(fileSrcs, false);
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
            fileRepository.findAllByUrlIdInAndDeletedIs(urls, false).stream()
                    .map(FileEntity::getCloudinaryPublicId)
                    .toList();
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
    return new DataResponse("Success");
  }

  @Override
  public DataResponse getAll() {
    List<BlogPostDTO> dtos =
        blogPostRepository.findAll().stream()
            .map(
                entity -> {
                  BlogPostDTO blogPostDTO = converter.toDTO(entity);
                  blogPostDTO.setBody(null);
                  return blogPostDTO;
                })
            .collect(Collectors.toList());
    return new DataResponse(dtos);
  }

  @Override
  public DataResponse getOneById(long id) {
    BlogPostDTO dto =
        converter.toDTO(
            blogPostRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("NOT_FOUND_BLOG_POST")));
    return new DataResponse(dto);
  }

  public List<String> getUrlOfFirstListWhichSecondListNotContain(
      List<String> firstList, List<String> secondList) {
    // getDeletedBlogImageUrl
    return firstList.stream().filter(item -> !secondList.contains(item)).toList();
  }

  public List<String> detectedImageSrcList(String html) {
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
