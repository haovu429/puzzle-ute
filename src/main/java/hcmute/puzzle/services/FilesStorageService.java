package hcmute.puzzle.services;

import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.exception.NotFoundException;
import hcmute.puzzle.exception.PartialFailureException;
import hcmute.puzzle.model.CloudinaryUploadFileResponse;
import hcmute.puzzle.model.enums.FileCategory;

import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public interface FilesStorageService {
  Map uploadFile(String imageName, MultipartFile file, String locationStorage);

  boolean deleteFile(String imageName, FileCategory category,  UserEntity deleter) throws NotFoundException;

  boolean deleteMultiFile(List<String> publicIds, UserEntity deleter) throws PartialFailureException;

  String uploadFileWithFileTypeReturnUrl(
      UserEntity author, String keyName, MultipartFile file, FileCategory fileCategory)
      throws NotFoundException;

  String processFileName(String keyValue, FileCategory fileCategory);

  CloudinaryUploadFileResponse uploadFileReturnResponseObject(String fileName, MultipartFile file, String fileLocation, UserEntity uploader);

  public List<String> detectedImageSrcList(String html);

  List<String> getDeletedImageSrcs(List<String> oldList, List<String> newList);
}

