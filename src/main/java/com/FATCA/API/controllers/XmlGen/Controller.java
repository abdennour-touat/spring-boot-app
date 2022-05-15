package com.FATCA.API.controllers.XmlGen;

import com.FATCA.API.converter.XmlCsvGen;
import com.FATCA.API.fileStorage.FilesStorageService;
import com.FATCA.API.fileStorage.FilesStorageServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

//import javax.swing.text.Element;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="api/v1/csv",produces = { "application/xml", "text/xml" }, consumes = MediaType.ALL_VALUE)
@Slf4j
public class Controller {
    private final CsvService csvService;
    @Autowired
    private final FilesStorageService filesStorageService;
//    @PostMapping("/convert")
    @PostMapping(path="/convert" )
    public ResponseEntity<?> convertToXml(@RequestParam("file")MultipartFile file){
        List<String[]> data =csvService.getCsvFile(file);
        String result = null;
        try {
            result = XmlCsvGen.generate(data, filesStorageService.getTemplate());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(result, headers, HttpStatus.CREATED);
    }

}
