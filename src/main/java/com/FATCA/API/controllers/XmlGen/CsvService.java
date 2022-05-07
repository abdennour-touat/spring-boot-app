package com.FATCA.API.controllers.XmlGen;

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
}