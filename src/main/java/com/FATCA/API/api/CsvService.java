package com.FATCA.API.api;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
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