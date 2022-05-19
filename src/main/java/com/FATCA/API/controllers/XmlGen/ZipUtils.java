package com.FATCA.API.controllers.XmlGen;

import com.FATCA.API.fileStorage.FilesStorageService;
import com.FATCA.API.fileStorage.ZipUtilsService;
import lombok.RequiredArgsConstructor;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;

@RequiredArgsConstructor
public final class ZipUtils implements ZipUtilsService {
        private final FilesStorageService filesStorageService;
    public ZipFile zipWithPassword(String data, String fileName){
        try
        {
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setEncryptFiles(true);
            zipParameters.setCompressionLevel(CompressionLevel.HIGHER);
            zipParameters.setEncryptionMethod(EncryptionMethod.AES);

            File f = new File("uploads/"+fileName+".xml");
            filesStorageService.insert(f.getName(), data);
            ZipFile zipFile = new ZipFile(fileName+".zip", "password".toCharArray());
            zipFile.addFile(f, zipParameters);
            return zipFile;

        }catch (IOException e ){
            e.printStackTrace();
        }
    return null;
    }

}
