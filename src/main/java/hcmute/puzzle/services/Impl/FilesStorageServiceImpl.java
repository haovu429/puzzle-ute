package hcmute.puzzle.services.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import hcmute.puzzle.services.FilesStorageService;
import hcmute.puzzle.utils.CloudinaryUtil;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;

@Service
public class FilesStorageServiceImpl implements FilesStorageService {

  @Override
  public Map uploadAvatarImage(String imageName, MultipartFile file) {
    Cloudinary cloudinary = CloudinaryUtil.getCloudinary();

    try {
      // Save file to resource
      /*FileOutputStream output = new FileOutputStream(fileLocation);
      output.write(file.getBytes());
      output.close();*/

      // File tempFile = new File(fileLocation);
      // File tempFile = new File();
      // file.transferTo(tempFile);

      // InputStream inputStream = new FileInputStream(tempFile);
      // byte[] sourceBytes = IOUtils.toByteArray(inputStream);

      byte[] image = Base64.encodeBase64(file.getBytes());
      String encodedString = new String(image);

      // java.util.Base64.encode(FileUtils.readFileToByteArray(file));

      // Upload the image
      Map params1 =
          ObjectUtils.asMap(
              "public_id", imageName,
              "use_filename", true,
              "unique_filename", false,
              "overwrite", true);

      // String encodedString = Base64.getEncoder().encodeToString(sourceBytes);

      //            Map result =
      //                    cloudinary
      //                            .uploader()
      //                            .upload(
      //                                    fileLocation,
      //                                    params1);

      Map result = cloudinary.uploader().upload("data:image/png;base64," + encodedString, params1);
      return result;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
