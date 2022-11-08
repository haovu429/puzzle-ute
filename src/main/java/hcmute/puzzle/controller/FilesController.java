package hcmute.puzzle.controller;

import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.filter.JwtAuthenticationFilter;
import hcmute.puzzle.repository.UserRepository;
import hcmute.puzzle.services.FilesStorageService;
import hcmute.puzzle.utils.Constant;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api")
@CrossOrigin(origins = {Constant.LOCAL_URL, Constant.ONLINE_URL})
public class FilesController {

  @Autowired FilesStorageService storageService;

  @Autowired JwtAuthenticationFilter jwtAuthenticationFilter;

  @Autowired UserRepository userRepository;

  @PostMapping("/upload-avatar")
  public ResponseObject uploadFile(
      @RequestParam("file") MultipartFile file,
      @RequestHeader(value = "Authorization") String token) {
    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);

    if (linkUser.isEmpty()) {
      throw new CustomException("Not found account");
    }

    String fileName = linkUser.get().getEmail() + "_avatar";

    Map response = null;

    try {
      // push to storage cloud
      response = storageService.uploadAvatarImage(fileName, file);

    } catch (Exception e) {
      e.printStackTrace();
    }

    if (response == null) {
      throw new CustomException("Upload image failure");
    }

    if (response.get("secure_url") == null) {
      throw new CustomException("Can't get url from response of storage cloud");
    }

    String url = response.get("secure_url").toString();

    linkUser.get().setAvatar(url);

    userRepository.save(linkUser.get());

    return new ResponseObject(200, "Upload image success", response);
  }

  @GetMapping("/delete-avatar")
  public ResponseObject deleteFile(
          @RequestHeader(value = "Authorization") String token) {
    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);

    if (linkUser.isEmpty()) {
      throw new CustomException("Not found account");
    }

    String fileName = linkUser.get().getEmail() + "_avatar";

    Map response;

    response = storageService.deleteAvatarImage(fileName);

    if (response == null) {
      throw new CustomException("Delete image failure, not response");
    }

    if (response.get("result") == null) {
      throw new CustomException("Can't get url from response of storage cloud");
    }

    String result = response.get("result").toString();

    if (!result.equals("ok")) {
      return new ResponseObject(200, "Delete image failure, response isn't ok", response);
      //throw new CustomException("Delete image failure, response isn't ok");
    }

    linkUser.get().setAvatar(null);

    userRepository.save(linkUser.get());

    return new ResponseObject(200, "Delete image success", response);
  }

  //    @GetMapping("/files")
  //    public ResponseEntity<List<FileInfo>> getListFiles() {
  //        List<FileInfo> fileInfos = storageService.loadAll().map(path -> {
  //            String filename = path.getFileName().toString();
  //            String url = MvcUriComponentsBuilder
  //                    .fromMethodName(FilesController.class, "getFile",
  // path.getFileName().toString()).build().toString();
  //
  //            return new FileInfo(filename, url);
  //        }).collect(Collectors.toList());
  //
  //        return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
  //    }
  //
  //    @GetMapping("/files/{filename:.+}")
  //    @ResponseBody
  //    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
  //        Resource file = storageService.load(filename);
  //        return ResponseEntity.ok()
  //                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
  // file.getFilename() + "\"").body(file);
  //    }
}
