package com.FATCA.API.fileStorage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
public interface FilesStorageService {
    public void init();

//    void insert(String filename, long offset, byte[] content) throws IOException;

    public void save(MultipartFile file, String path);
    public Resource load(String filename, String path);
    public void deleteAll();
    String getTemplateString() throws  Exception;
    public Stream<Path> loadAll();
    public void insert(String filename, String content) throws IOException;
    public Stream<Path> getTemplate() throws Exception;

    Resource loadFile(String filename);

    public Stream<Path> getXSDFiles();

}