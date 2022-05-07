package com.FATCA.API.controllers.adminAPI;

import com.FATCA.API.converter.XmlFromXsdGen;
import com.FATCA.API.fileStorage.FileInfo;
import com.FATCA.API.fileStorage.FilesStorageService;
import com.FATCA.API.fileStorage.ResponseMessage;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="api/v1/admin")
@Slf4j
public class AdminController {
    @Autowired
    FilesStorageService storageService;
    @GetMapping(path = "/getTemp",produces = { "application/xml", "text/xml" }, consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> getTemplate( templateForm form){
        String schema = storageService.load(form.getXsdName(), "xsd");
        String result = XmlFromXsdGen.generateXml(schema, form.getLocalPart());
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(result, headers, HttpStatus.CREATED);
    }
    @PostMapping("/uploadXSD")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";
             try {
                 storageService.save(file, "xsd");
                 message = "Uploaded the file successfully: " + file.getOriginalFilename();
                 return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
             } catch (Exception e) {
                 message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                 return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
             }


    }
    @GetMapping("/files")
    public ResponseEntity<List<FileInfo>> getListFiles() {
        List<FileInfo> fileInfos = storageService.loadAll().map(path -> {
            String filename = path.getFileName().toString();
            String url = MvcUriComponentsBuilder
                    .fromMethodName(AdminController.class, "getFile", path.getFileName().toString()).build().toString();
            return new FileInfo(filename, url);
        }).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
    }
    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<?> getFile(@PathVariable String filename) {
        String file = storageService.load(filename, "");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file + "\"").body(file);
    }
}
@Data
class templateForm{
    private String localPart;
    private String xsdName;
}
