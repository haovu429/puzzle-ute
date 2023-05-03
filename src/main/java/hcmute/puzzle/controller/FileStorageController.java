package hcmute.puzzle.controller;

import hcmute.puzzle.filter.JwtAuthenticationFilter;
import hcmute.puzzle.infrastructure.repository.UserRepository;
import hcmute.puzzle.services.FilesStorageService;
import hcmute.puzzle.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = {Constant.LOCAL_URL, Constant.ONLINE_URL})
public class FileStorageController {

  @Autowired FilesStorageService storageService;

  @Autowired JwtAuthenticationFilter jwtAuthenticationFilter;

  @Autowired UserRepository userRepository;

//  @PostMapping("/upload-blog-image")
//  public DataResponse uploadBlogImage(@RequestParam("file") MultipartFile file)
//      throws NotFoundException {
//    UserEntity user =
//        (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//    if (user == null) {
//      throw new NotFoundException();
//    }
//    String fileUrl =
//        storageService.uploadFileWithFileTypeReturnUrl(
//            user, user.getEmail(), file, FileCategory.IMAGE_AVATAR);
//    return new DataResponse(fileUrl);
//  }

  //    @GetMapping("/files")
  //    public ResponseEntity<List<FileInfo>> getListFiles() {
  //        List<FileInfo> fileInfos = storageService.loadAll().map(path -> {
  //            String filename = path.getFileName().toString();
  //            String url = MvcUriComponentsBuilder
  //                    .fromMethodName(FileStorageController.class, "getFile",
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
