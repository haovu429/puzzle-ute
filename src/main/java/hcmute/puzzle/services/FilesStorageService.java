package hcmute.puzzle.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface FilesStorageService {
    Map uploadAvatarImage(String imageName, MultipartFile file);

    Map deleteAvatarImage(String imageName);
}
