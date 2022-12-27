package hcmute.puzzle.services.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import hcmute.puzzle.services.FilesStorageService;
import hcmute.puzzle.utils.CloudinaryUtil;
import hcmute.puzzle.utils.Constant;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class FilesStorageServiceImpl implements FilesStorageService {

  @Override
  public Map uploadAvatarImage(String imageName, MultipartFile file, String locationStorage) {
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
              imageName,
              "use_filename",
              true,
              "unique_filename",
              false,
              "overwrite",
              true);

      Map result = cloudinary.uploader().upload("data:image/png;base64," + encodedString, params1);
      return result;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public Map deleteAvatarImage(String imageName) {
    Cloudinary cloudinary = CloudinaryUtil.getCloudinary();

    try {
      // Destroy the image
      Map params1 = ObjectUtils.asMap("resource_type", "image", "folder", "puzzle_ute/user/avatar", "type", "upload", "invalidate", true);

      Map result = cloudinary.uploader().destroy(Constant.STORAGE_IMAGE_LOCATION + "/" + imageName, params1);
      return result;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
