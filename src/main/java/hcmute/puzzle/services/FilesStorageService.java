package hcmute.puzzle.services;

import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.model.enums.FileType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface FilesStorageService {
    Map uploadAvatarImage(String imageName, MultipartFile file, String locationStorage);

    Map deleteAvatarImage(String imageName);

//    String updateAvatarReturnUrl(Object id, MultipartFile file, String prefix);

    String uploadFileWithFileTypeReturnUrl(UserEntity author, String keyName, MultipartFile file, FileType fileType);

    String processFileName(String keyValue, FileType fileType);

    String uploadFileReturnUrl(String fileName, MultipartFile file);
}
