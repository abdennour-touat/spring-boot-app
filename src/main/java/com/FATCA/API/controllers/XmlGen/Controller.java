package com.FATCA.API.controllers.XmlGen;

import com.FATCA.API.controllers.adminAPI.AdminController;
import com.FATCA.API.converter.XmlCsvGen;
import com.FATCA.API.fileStorage.FileInfo;
import com.FATCA.API.fileStorage.FilesStorageService;
import com.FATCA.API.fileStorage.ResponseMessage;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

//import javax.swing.text.Element;

import javax.swing.*;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/v1/csv", produces = {"application/xml", "text/xml"}, consumes = MediaType.ALL_VALUE)
@Slf4j
public class Controller {
    @Autowired
    private final CsvService csvService;
    @Autowired
    private final FilesStorageService filesStorageService;
//    private final ZipUtils zipUtils;

    //    @PostMapping("/convert")
    @PostMapping(path = "/convert")
    public ResponseEntity<?> convertToXml(@RequestBody ArrayData data) {
//        List<String[]> data =csvService.getCsvFile(file);
        String result = null;
        ByteArrayResource resource = null;
        try {
            result = XmlCsvGen.generate(data.getTable(), filesStorageService.getTemplateString());
            resource = new ByteArrayResource(result.getBytes());
            HttpHeaders headers = new HttpHeaders();
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @PostMapping(path="/convertcompressed" )
    public ResponseEntity<?> convertSecured(@RequestBody ArrayData data) throws MalformedURLException {
//        List<String[]> data = csvService.getCsvFile(file);
        String result = null;
        ZipFile compressed = null;
        char[] password = csvService.generatePassword(14);
        try {
            result = XmlCsvGen.generate(data.getTable(), filesStorageService.getTemplateString());
            compressed = csvService.zipWithPassword(result, data.getFilename(), password);

            System.out.println(password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
      String url = MvcUriComponentsBuilder
                .fromMethodName(Controller.class, "getZip",compressed.getFile().getName() , "zip").build().toString();
        HttpHeaders headers = new HttpHeaders();
        headers.add("password", String.valueOf(password));
        return  ResponseEntity.ok().headers(headers).body(url);
//
    }
    @GetMapping("/files/{filename:.+}/{path:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getZip(@PathVariable String filename, @PathVariable String path) throws IOException, InterruptedException {
        Resource file = filesStorageService.load(filename, path);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }


    @Data
    static
    class ArrayData {
        private List<List<HashMap<String, String>>> table;
        private String filename;
    }
}
