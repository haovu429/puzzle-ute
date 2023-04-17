package hcmute.puzzle.services.impl;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.BlogPostDTO;
import hcmute.puzzle.entities.BlogPostEntity;
import hcmute.puzzle.entities.FileEntity;
import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.repository.BlogPostRepository;
import hcmute.puzzle.repository.FileRepository;
import hcmute.puzzle.response.DataResponse;
import hcmute.puzzle.services.BlogPostService;
import hcmute.puzzle.services.FilesStorageService;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlogPostServiceImpl implements BlogPostService {

  @Autowired private Converter converter;

  @Autowired private BlogPostRepository blogPostRepository;

  @Autowired private FileRepository fileRepository;

  @Autowired private FilesStorageService filesStorageService;

  @Override
  public DataResponse save(BlogPostDTO dto) {
    BlogPostEntity entity = converter.toEntity(dto);
    entity = blogPostRepository.save(entity);
    return new DataResponse(converter.toDTO(entity));
  }

  @Override
  public DataResponse update(BlogPostDTO dto, long id, UserEntity updater) {
    BlogPostEntity entity = blogPostRepository.getById(id);
    if (entity != null) {
      if (dto.getUserId() != entity.getCreatedBy().getId()) {
        throw new CustomException("You don't have rights for this blog post");
      }
      entity.updateFromDTO(dto);
      blogPostRepository.save(entity);

      try {
        // release file don't use
        List<String> oldSrcs = detectedImageSrcList(entity.getBody());
        List<String> newSrcs = detectedImageSrcList(dto.getBody());
        List<String> urlToDelete = getDeletedBlogImageUrl(oldSrcs, newSrcs);
        List<String> publicIdsToDelete =
            fileRepository.findAllByUrlIdInAndDeletedIs(urlToDelete, false).stream()
                .map(FileEntity::getCloudinaryPublicId)
                .toList();
        if (!publicIdsToDelete.isEmpty() && publicIdsToDelete != null)
          filesStorageService.deleteMultiFile(publicIdsToDelete, updater);
      } catch (Exception e) {
        e.printStackTrace();
      }

      return new DataResponse(converter.toDTO(entity));
    }
    throw new CustomException("Not found blog post");
  }

  @Override
  public DataResponse delete(long id) {
    blogPostRepository.deleteById(id);
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
    BlogPostDTO dto = converter.toDTO(blogPostRepository.findById(id).get());
    return new DataResponse(dto);
  }

  public List<String> getDeletedBlogImageUrl(List<String> oldList, List<String> newList) {
    List<String> deletedImageSrcs =
        oldList.stream().filter(item -> !newList.contains(item)).toList();
    return deletedImageSrcs;
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
