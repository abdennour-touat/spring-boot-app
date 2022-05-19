package com.FATCA.API.fileStorage;

import net.lingala.zip4j.ZipFile;

public interface ZipUtilsService {
    public ZipFile zipWithPassword(String data, String fileName);
}
