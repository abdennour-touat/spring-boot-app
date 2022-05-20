package com.FATCA.API.controllers.XmlGen;

import com.FATCA.API.converter.XmlCsvGen;
import com.FATCA.API.fileStorage.FilesStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

//import javax.swing.text.Element;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="api/v1/csv",produces = { "application/xml", "text/xml" }, consumes = MediaType.ALL_VALUE)
@Slf4j
public class Controller {
    private final CsvService csvService;
    @Autowired
    private final FilesStorageService filesStorageService;
//    private final ZipUtils zipUtils;

//    @PostMapping("/convert")
    @PostMapping(path="/convert" )
    public ResponseEntity<?> convertToXml(@RequestParam("file")MultipartFile file){
        List<String[]> data =csvService.getCsvFile(file);
        String result = null;
        ByteArrayResource resource;
        try {
            result = XmlCsvGen.generate(data, filesStorageService.getTemplate());
            resource = new ByteArrayResource(result.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        HttpHeaders headers = new HttpHeaders();
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
    @PostMapping(path="/convertcompressed" )
    public ResponseEntity<?> convertSecured(@RequestParam("file")MultipartFile file) {
        List<String[]> data = csvService.getCsvFile(file);
        String result = null;
        ZipFile compressed = null;
        char[] password = csvService.generatePassword(10);
        byte[] stream;
        ByteArrayResource resource;
        try {
            result = XmlCsvGen.generate(data, filesStorageService.getTemplate());
            compressed = csvService.zipWithPassword(result, file.getOriginalFilename(), password);
             stream = compressed.getInputStream(compressed.getFileHeaders().get(0)).readAllBytes();
             resource = new ByteArrayResource(stream);
             Files.delete(compressed.getFile().toPath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("password", password.toString());
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(compressed.getBufferSize())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

}
