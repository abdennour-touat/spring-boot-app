package com.FATCA.API.fileStorage;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
public interface FilesStorageService {
    public void init();
    public void save(MultipartFile file, String path);
    public String load(String filename, String path);
    public void deleteAll();
    public Stream<Path> loadAll();
}