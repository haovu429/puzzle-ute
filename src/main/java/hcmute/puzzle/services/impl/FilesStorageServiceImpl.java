package hcmute.puzzle.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import hcmute.puzzle.configuration.security.CustomUserDetails;
import hcmute.puzzle.exception.*;
import hcmute.puzzle.infrastructure.entities.File;
import hcmute.puzzle.infrastructure.entities.FileType;
import hcmute.puzzle.infrastructure.entities.User;
import hcmute.puzzle.infrastructure.models.CloudinaryUploadFileResponse;
import hcmute.puzzle.infrastructure.models.enums.FileCategory;
import hcmute.puzzle.infrastructure.repository.FileRepository;
import hcmute.puzzle.infrastructure.repository.FileTypeRepository;
import hcmute.puzzle.services.FilesStorageService;
import hcmute.puzzle.utils.CloudinaryUtil;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static hcmute.puzzle.utils.Constant.*;

@Service
public class FilesStorageServiceImpl implements FilesStorageService {

  Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
  @Autowired private FileRepository fileRepository;

  @Autowired private FileTypeRepository fileTypeRepository;

  @Autowired CloudinaryUtil cloudinaryUtil;

  @Override
  public Map uploadFile(String publicId, MultipartFile file, String locationStorage) {
    if (file == null) {
      throw new FileStorageException("FILE_UPLOAD_IS_NULL");
    }
    Cloudinary cloudinary = cloudinaryUtil.getCloudinary();

    try {
      byte[] image = Base64.encodeBase64(file.getBytes(), false);
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
  public boolean deleteFile(
      String key, FileCategory category, boolean deleteByUrl)
      throws NotFoundException {
    // if deleteByUrl = true => key is url else key is public id

    // "puzzle_ute/user/avatar"
    Cloudinary cloudinary = cloudinaryUtil.getCloudinary();

    File file;
    if (deleteByUrl) {
      file =
          fileRepository.findAllByUrlAndDeletedIs(key, false).orElseThrow(NotFoundException::new);
    } else {
      file = fileRepository.findByCloudinaryPublicId(key).orElseThrow(NotFoundException::new);
    }

    if (file == null) {
      logger.error("Can't delete file with key (deleteByUrl= " + deleteByUrl + ") :" + key);
      return false;
    }

    FileType fileType =
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

      Map result = cloudinary.uploader().destroy(file.getCloudinaryPublicId(), params1);

      if (result == null) {
        throw new CustomException("Upload image failure");
      }

      file.setIsDeleted(true);
      fileRepository.save(file);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  // @Override
  public boolean deleteMultiFile(List<String> publicIds, User deleter)
      throws PartialFailureException {
    // "puzzle_ute/user/avatar"
    Cloudinary cloudinary = cloudinaryUtil.getCloudinary();
    // Check public_id exists in db
    List<File> fileInfoFromDB =
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
            fileEntity.setIsDeleted(true);
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

  public Optional<String> uploadFileWithFileTypeReturnUrl(
          String keyName, MultipartFile file, FileCategory fileCategory, boolean saveDB) throws NotFoundException {

    FileType fileType =
            fileTypeRepository.findByCategory(fileCategory).orElseThrow(() -> new NotFoundDataException("Not found file type category"));

    String fileName = processFileName(keyName, fileCategory);
    CloudinaryUploadFileResponse response =
            uploadFileReturnResponseObject(fileName, file, fileType.getLocation());
    String fileUrl = response.getSecure_url();

    if (fileUrl == null || fileUrl.isEmpty() || fileUrl.isBlank()) {
      throw new FileStorageException("UPLOAD_FILE_IS_EMPTY");
    }

    if (saveDB) {
      // Save file info to db.
      File fileEntity =
              File.builder()
                  .name(fileName.replace(" ", ""))
                  .category(fileCategory.getValue())
                  .type(fileType.getType().getValue())
                  .location(fileType.getLocation())
                  .extension(Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename())))
                  .url(fileUrl)
                  .cloudinaryPublicId(response.getPublic_id())
                  .build();
      fileRepository.save(fileEntity);
    }


    return Optional.of(fileUrl);
  }

  public Optional<File> uploadFileWithFileTypeReturnFileEntity(
          String keyName, MultipartFile file, FileCategory fileCategory) throws NotFoundException {


    User author =
            ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                    .getUser();

    FileType fileType =
            fileTypeRepository.findByCategory(fileCategory).orElseThrow(NotFoundException::new);

    String fileName = processFileName(keyName, fileCategory);
    CloudinaryUploadFileResponse response =
            uploadFileReturnResponseObject(fileName, file, fileType.getLocation());
    String fileUrl = response.getSecure_url();

    if (fileUrl == null || fileUrl.isEmpty() || fileUrl.isBlank()) {
      throw new FileStorageException("UPLOAD_FILE_IS_EMPTY");
    }

    // Save file info to db.
    File fileEntity =
            File.builder()
                .name(fileName.replace(" ", ""))
                .category(fileCategory.getValue())
                .type(fileType.getType().getValue())
                .location(fileType.getLocation())
                .extension(Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename())))
                .url(fileUrl)
                .cloudinaryPublicId(response.getPublic_id())
                .build();

    fileEntity = fileRepository.save(fileEntity);

    return Optional.of(fileEntity);
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
      String fileName, MultipartFile file, String fileLocation) {

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
    return oldList.stream().filter(item -> !newList.contains(item)).collect(Collectors.toList());
  }
}
