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

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    //password genrator..
    public char[] generatePassword(int length) {
        String capitalCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String specialCharacters = "!@#$";
        String numbers = "1234567890";
        String combinedChars = capitalCaseLetters + lowerCaseLetters + specialCharacters + numbers;
        Random random = new Random();
        char[] password = new char[length];

        password[0] = lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length()));
        password[1] = capitalCaseLetters.charAt(random.nextInt(capitalCaseLetters.length()));
        password[2] = specialCharacters.charAt(random.nextInt(specialCharacters.length()));
        password[3] = numbers.charAt(random.nextInt(numbers.length()));

        for (int i = 4; i < length; i++) {
            password[i] = combinedChars.charAt(random.nextInt(combinedChars.length()));
        }
        return password;
    }

    public ZipFile zipWithPassword(String data, String fileName, char[] password) throws IOException {
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setEncryptFiles(true);

        zipParameters.setCompressionLevel(CompressionLevel.NORMAL);
        zipParameters.setEncryptionMethod(EncryptionMethod.AES);
        zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);


        File folder = new File("uploads/xmlFile");
        if (folder.mkdir()) {
            File f = new File("uploads/" + folder.getName() + "/" + fileName + ".xml");
            ZipFile zipFile = null;
            if (f.createNewFile()) {
                FileWriter fw = new FileWriter(f);
                fw.write(data);
                fw.close();
                zipFile = new ZipFile("uploads/" + fileName + ".zip", password);

                zipFile.addFolder(folder, zipParameters);


            } else {
                System.out.println("file exists");
            }
            File[] files = folder.listFiles();
            assert files != null;
            for(File file : files) {
                System.out.println(file + " deleted.");

                Files.delete(file.toPath());
            }
            Files.delete(folder.toPath());
            if (folder.delete()) {
                System.out.println("folder deleted");
            }
            return zipFile;
        }

        return null;
    }


}