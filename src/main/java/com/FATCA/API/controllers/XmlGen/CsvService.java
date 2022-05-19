package com.FATCA.API.controllers.XmlGen;

import com.FATCA.API.fileStorage.FilesStorageService;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

import java.util.ArrayList;
import java.util.List;

@Service
//@Component
public class CsvService {
    public List<String[]> getCsvFile(MultipartFile file) {
        if (!file.isEmpty()) {
            BufferedReader br;
            List<String[]> result = new ArrayList<>();
            try {
                String[] line;
                String ln;
                InputStream is = file.getInputStream();
                br = new BufferedReader(new InputStreamReader(is));

                while ((ln = br.readLine()) != null) {

                    line = ln.split(";");

                    result.add(line);
                }
                return result;

            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        return null;
    }
    public ZipFile zipWithPassword(String data, String fileName, FilesStorageService filesStorageService) throws IOException {
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setEncryptFiles(true);
        zipParameters.setCompressionLevel(CompressionLevel.NORMAL);
        zipParameters.setEncryptionMethod(EncryptionMethod.AES);
        zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);


//            Path p = Files.createFile(Paths.get("uploads/"+fileName+".xml"));
        File folder =  new File("uploads/xmlFile");
        if (folder.mkdir()) {


            File f = new File("uploads/"+folder.getName()+"/" + fileName + ".xml");
            if (f.createNewFile()) {
                FileWriter fw = new FileWriter(f);
                fw.write(data);
                fw.close();
//            filesStorageService.insert("uploads/"+fileName+".xml", data);
                ZipFile zipFile = new ZipFile("uploads/"+fileName + ".zip", "password".toCharArray());
//        zipFile.setPassword("password".toCharArray());
//                zipFile.setPassword("password".toCharArray());
                zipFile.addFolder(folder, zipParameters );
//                zipFile.addFile(f);


//                if(f.delete()){
//                    System.out.println("file deleted");
//                }
                return zipFile;
            } else {
                System.out.println("file exists");
            }
        }

        return null;
    }


}