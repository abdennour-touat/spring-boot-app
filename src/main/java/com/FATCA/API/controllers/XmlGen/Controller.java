package com.FATCA.API.controllers.XmlGen;

import com.FATCA.API.converter.XmlCsvGen;
import com.FATCA.API.fileStorage.FilesStorageService;
import com.FATCA.API.fileStorage.ZipUtilsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
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
//    private final ZipUtils zipUtils;

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
    @PostMapping(path="/convertcompressed" )
    public ResponseEntity<?> convertSecured(@RequestParam("file")MultipartFile file){
        List<String[]> data =csvService.getCsvFile(file);
        String result = null;
        ZipFile compressed = null;
        try {
            result = XmlCsvGen.generate(data, filesStorageService.getTemplate());
            compressed = csvService.zipWithPassword(result, file.getOriginalFilename(), filesStorageService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(compressed, headers, HttpStatus.CREATED);
    }
//    @PostMapping(path="/convert" )
//    public ResponseEntity<?> convertToXmlZipped(@RequestParam("file")MultipartFile file){
//        List<String[]> data =csvService.getCsvFile(file);
//        String result = null;
//        try {
//            result = XmlCsvGen.generate(data, filesStorageService.getTemplate());
//            File zipped = new File(filePath);
//            String zipFileName = file.getName().concat(".zip");
//
//            FileOutputStream fos = new FileOutputStream(zipFileName);
//            ZipOutputStream zos = new ZipOutputStream(fos);
//
//            zos.putNextEntry(new ZipEntry(file.getName()));
//
//            byte[] bytes = Files.readAllBytes(Paths.get(filePath));
//            zos.write(bytes, 0, bytes.length);
//            zos.closeEntry();
//            zos.close();
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        HttpHeaders headers = new HttpHeaders();
//        return new ResponseEntity<>(result, headers, HttpStatus.CREATED);
//    }

}
