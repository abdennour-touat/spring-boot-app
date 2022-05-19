package com.FATCA.API.fileStorage;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.stream.Stream;

public class FilesStorageServiceImpl implements FilesStorageService {
    private final Path root = Paths.get("uploads/");
    private final Path xsdStore = Paths.get("uploads/xsdFiles/");

    private final Path templateStore = Paths.get("uploads/templateFiles/");
    @Override
    public void init() {
        try {
            Files.createDirectory(root);
            Files.createDirectory(xsdStore);
            Files.createDirectory(templateStore);
            Files.createFile(Paths.get("uploads/main.txt"));
//            Files.createFile(Paths.get("uploads/finalFile.xml"));
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }
    @Override
    public void insert(String filename, String content)  {
        try {
            FileWriter myWriter = new FileWriter(filename);
            myWriter.write(content);

            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }
    @Override
    public void save(MultipartFile file, String path) {
         switch (path){
             case "xsd":
                 try {
                     Files.copy(file.getInputStream(), xsdStore.resolve(Objects.requireNonNull(file.getOriginalFilename())));
                 } catch (Exception e) {
                     throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
                 }
                 break;
             case "template":
                 try {
                     System.out.println("hello");
                     Files.copy(file.getInputStream(), templateStore.resolve(Objects.requireNonNull(file.getOriginalFilename())));
//                     File directory = new File(templateStore.toUri());
//                     BasicFileAttributes basicFileAttributes = Files.readAttributes(directory.toPath(), BasicFileAttributes.class);
//                     if (basicFileAttributes.isDirectory()) {
//                         String[] files = directory.list();
//                         if (basicFileAttributes.size() < 0) {
//                         } else {
//                             throw new Exception("there's already a template file, delete the old one to insert a new one");
//                         }
//                     }
                 } catch (Exception e) {
                     throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
                 }
                 break;
             default:
                 try {
                     Files.copy(file.getInputStream(), root.resolve(Objects.requireNonNull(file.getOriginalFilename())));
                 } catch (Exception e) {
                     throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
                 }
                 break;
         }
    }
    @Override
    public String load(String filename, String path) {
        switch (path){
            case "xsd":
                try {
                    Path file = xsdStore.resolve(filename);
                    Resource resource = new UrlResource(file.toUri());
                    if (resource.exists() ) {
                        return file.toString();
                    } else {
                        throw new RuntimeException("Could not read the file!");
                    }
                } catch (MalformedURLException e) {
                    throw new RuntimeException("Error: " + e.getMessage());
                }
            case "template":
                try {
                    Path file = templateStore.resolve(filename);
                    Resource resource = new UrlResource(file.toUri());
                    if (resource.exists() ) {
                        return file.toString();
                    } else {
                        throw new RuntimeException("Could not read the file!");
                    }
                } catch (MalformedURLException e) {
                    throw new RuntimeException("Error: " + e.getMessage());
                }
            default:
                try {
                    Path file = root.resolve(filename);
                    Resource resource = new UrlResource(file.toUri());
                    if (resource.exists()) {
                        return file.toString();
                    } else {
                        throw new RuntimeException("Could not read the file!");
                    }
                } catch (MalformedURLException e) {
                    throw new RuntimeException("Error: " + e.getMessage());
                }

        }
    }
    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }
    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.root, 2).filter(path -> !path.equals(this.root)).map(this.root::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }
    public String getTemplate() throws Exception {
//        System.out.println(templateStore.toUri());
      File dir = new File("uploads/templateFiles");
      if (dir.isDirectory()){
          return Objects.requireNonNull(dir.listFiles())[0].getAbsolutePath();
      }else {
          throw new Exception("directory is empty");
      }
    }
}

