package com.FATCA.API.fileStorage;
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
    public String load(String filename, String path);
    public void deleteAll();
    public Stream<Path> loadAll();
    public void insert(String filename, String content) throws IOException;
    public String getTemplate() throws Exception;
}