package hcmute.puzzle.services.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import hcmute.puzzle.services.FilesStorageService;
import hcmute.puzzle.utils.CloudinaryUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

@Service
public class FilesStorageServiceImpl implements FilesStorageService {

    @Override
    public Map uploadAvatarImage(String imageName, MultipartFile file){
        Cloudinary cloudinary = CloudinaryUtil.getCloudinary();



        try {
            byte[] sourceBytes = IOUtils.toByteArray(file.getInputStream());

            File localFile = new File("static/images/" + imageName);

            System.out.println("Path: " +localFile.getCanonicalPath());

            // Upload the image
            Map params1 =
                    ObjectUtils.asMap(
                            "public_id",imageName,
                            "use_filename", true,
                            "unique_filename", false,
                            "overwrite", true);

            String encodedString = Base64.getEncoder().encodeToString(sourceBytes);

            Map result =
                    cloudinary
                            .uploader()
                            .upload(
                                    imageName,
                                    params1);

//            Map result =
//                    cloudinary
//                            .uploader()
//                            .upload(
//                                    "data:image/png;base64,"+encodedString,
//                                    params1);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
