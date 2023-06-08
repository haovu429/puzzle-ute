package hcmute.puzzle.services;

import hcmute.puzzle.infrastructure.entities.File;
import hcmute.puzzle.infrastructure.entities.User;
import hcmute.puzzle.exception.NotFoundException;
import hcmute.puzzle.exception.PartialFailureException;
import hcmute.puzzle.infrastructure.models.CloudinaryUploadFileResponse;
import hcmute.puzzle.infrastructure.models.enums.FileCategory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

public interface FilesStorageService {
    Map uploadFile(String imageName, MultipartFile file, String locationStorage);

    boolean deleteFile(String imageName, FileCategory category, User deleter, boolean deleteByUrl)
            throws NotFoundException;

    boolean deleteMultiFile(List<String> publicIds, User deleter) throws PartialFailureException;

    Optional<String> uploadFileWithFileTypeReturnUrl(String keyName
            , MultipartFile file, FileCategory fileCategory, boolean saveDB)
            throws NotFoundException;

    Optional<File> uploadFileWithFileTypeReturnFileEntity(String keyName
            , MultipartFile file, FileCategory fileCategory)
            throws NotFoundException;


    String processFileName(String keyValue, FileCategory fileCategory);

    CloudinaryUploadFileResponse uploadFileReturnResponseObject(String fileName
            , MultipartFile file, String fileLocation);

    public List<String> detectedImageSrcList(String html);

    List<String> getDeletedImageSrcs(List<String> oldList, List<String> newList);
}

