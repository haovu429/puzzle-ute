package hcmute.puzzle.services.impl;

import static hcmute.puzzle.utils.Constant.*;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import hcmute.puzzle.entities.FileEntity;
import hcmute.puzzle.entities.FileTypeEntity;
import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.exception.*;
import hcmute.puzzle.model.CloudinaryUploadFileResponse;
import hcmute.puzzle.model.enums.FileCategory;
import hcmute.puzzle.repository.FileRepository;
import hcmute.puzzle.repository.FileTypeRepository;
import hcmute.puzzle.services.FilesStorageService;
import hcmute.puzzle.utils.CloudinaryUtil;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FilesStorageServiceImpl implements FilesStorageService {

  @Autowired private FileRepository fileRepository;

  @Autowired private FileTypeRepository fileTypeRepository;

  @Override
  public Map uploadFile(String publicId, MultipartFile file, String locationStorage) {
    Cloudinary cloudinary = CloudinaryUtil.getCloudinary();

    try {
      byte[] image = Base64.encodeBase64(file.getBytes());
      String encodedString = new String(image);

      // Upload the image
      Map params1 =
          ObjectUtils.asMap(
              "folder",
              locationStorage,
              "public_id",
              publicId,
              "use_filename",
              true,
              "unique_filename",
              false,
              "overwrite",
              true);

      Map<String, Object> result =
          cloudinary.uploader().upload("data:image/png;base64," + encodedString, params1);

      if (result == null) {
        throw new UploadFailureException();
      }

      if (result.get("secure_url") == null) {
        throw new CustomException("Can't get url from response of storage cloud");
      }

      return result;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new HashMap<>();
  }

  @Override
  public boolean deleteFile(String publicId, FileCategory category, UserEntity deleter)
      throws NotFoundException {
    // "puzzle_ute/user/avatar"
    Cloudinary cloudinary = CloudinaryUtil.getCloudinary();

    FileEntity fileEntity =
        fileRepository.findByCloudinaryPublicId(publicId).orElseThrow(NotFoundException::new);

    FileTypeEntity fileType =
        fileTypeRepository.findByCategory(category).orElseThrow(NotFoundException::new);

    try {
      // Destroy the image
      Map params1 =
          ObjectUtils.asMap(
              "resource_type",
              fileType.getType().getValue(),
              "folder",
              fileType.getLocation(),
              "type",
              "upload",
              "invalidate",
              true);

      Map result = cloudinary.uploader().destroy(fileType.getLocation() + "/" + publicId, params1);

      if (result == null) {
        throw new CustomException("Upload image failure");
      }

      fileEntity.setDeleted(true);
      fileEntity.setUpdatedAt(new Date());
      fileEntity.setUpdatedBy(deleter.getEmail());
      fileRepository.save(fileEntity);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  // @Override
  public boolean deleteMultiFile(List<String> publicIds, UserEntity deleter)
      throws PartialFailureException {
    // "puzzle_ute/user/avatar"
    Cloudinary cloudinary = CloudinaryUtil.getCloudinary();
    // Check public_id exists in db
    List<FileEntity> fileInfoFromDB =
        fileRepository.findAllByCloudinaryPublicIdInAndDeletedIs(publicIds, false);

    // checkNotExistsPublicIdsInDB(publicIds, publicIdsFromDB);
    try {
      // Delete the image
      ApiResponse result = cloudinary.api().deleteResources(publicIds, ObjectUtils.emptyMap());

      if (result == null) {
        throw new CustomException("Delete image failure");
      }

      fileInfoFromDB.forEach(
          fileEntity -> {
            fileEntity.setDeleted(true);
            fileEntity.setUpdatedAt(new Date());
            fileEntity.setUpdatedBy(deleter.getEmail());
          });

      fileRepository.saveAll(fileInfoFromDB);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  //  private void checkNotExistsPublicIdsInDB(List<String> publicIds) {
  //
  //    List<FileEntity> fileInfoFromDB =
  //        fileRepository.findAllByCloudinaryPublicIdInAndDeletedIs(publicIds, false);
  //
  //    List<String> publicIdsFromDB =
  //        fileInfoFromDB.stream().map(FileEntity::getCloudinaryPublicId).toList();
  //    List<String> notExistIds =
  //        publicIds.stream()
  //            .filter(
  //                publicId ->
  //                    publicIdsFromDB.stream().noneMatch(idFromDb -> idFromDb.equals(publicId)))
  //            .toList();
  //
  //    if (!notExistIds.isEmpty()) {
  //      String msg = PartialFailureException.processingMsg(notExistIds);
  //      throw new PartialFailureException(msg);
  //    }
  //  }

  public String uploadFileWithFileTypeReturnUrl(
      UserEntity author, String keyName, MultipartFile file, FileCategory fileCategory)
      throws NotFoundException {

    FileTypeEntity fileType =
        fileTypeRepository.findByCategory(fileCategory).orElseThrow(NotFoundException::new);

    String fileName = processFileName(keyName, fileCategory);
    CloudinaryUploadFileResponse response =
        uploadFileReturnResponseObject(fileName, file, fileType.getLocation(), author);
    String fileUrl = response.getSecure_url();
    Date currentTime = new Date();

    // Save file info to db.
    FileEntity fileEntity =
        FileEntity.builder()
            .name(fileName.replace(" ", ""))
            .category(fileCategory.getValue())
            .type(fileType.getType().getValue())
            .location(fileType.getLocation())
            .extension(Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename())))
            .url(fileUrl)
            .created_by(author.getEmail())
            .updatedBy(author.getEmail())
            .updatedAt(currentTime)
            .cloudinaryPublicId(response.getPublic_id())
            .createdAt(currentTime)
            .build();

    fileRepository.save(fileEntity);

    return fileUrl;
  }

  public String processFileName(String keyValue, FileCategory fileCategory) {
    String pattern = "yyyy-MM-dd'T'HH:mm:ss";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    String date = simpleDateFormat.format(new Date());
    // System.out.println(date);
    return keyValue.concat(date).concat(getSuffixByFileType(fileCategory));
  }

  public String getSuffixByFileType(FileCategory fileCategory) {
    switch (fileCategory) {
      case IMAGE_AVATAR:
        return SUFFIX_AVATAR_FILE_NAME;
      case IMAGE_BLOG:
        return SUFFIX_BLOG_IMAGE_FILE_NAME;
      case IMAGE_COMPANY:
        return SUFFIX_COMPANY_IMAGE_FILE_NAME;
      default:
        return "";
    }
  }

  public CloudinaryUploadFileResponse uploadFileReturnResponseObject(
      String fileName, MultipartFile file, String fileLocation, UserEntity uploader) {

    Map<String, Object> response = null;
    CloudinaryUploadFileResponse respObject = new CloudinaryUploadFileResponse();
    try {
      // push to storage cloud
      response = uploadFile(fileName, file, fileLocation);

      final ObjectMapper mapper = new ObjectMapper(); // jackson's objectmapper
      respObject = mapper.convertValue(response, CloudinaryUploadFileResponse.class);

    } catch (Exception e) {
      e.printStackTrace();
      if (response != null) {
        respObject.setSecure_url(response.get("secure_url").toString());
      }
      e.printStackTrace();
    }

    return respObject;
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

  public List<String> getDeletedImageSrcs(List<String> oldList, List<String> newList) {
    return oldList.stream().filter(item -> !newList.contains(item)).toList();
  }
}
