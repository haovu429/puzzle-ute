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
