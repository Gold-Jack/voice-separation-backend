package com.voice.separation.util;

import cn.hutool.core.io.FileUtil;
import org.apache.hc.core5.http.ContentType;
import org.bson.types.Binary;
import org.springframework.lang.Nullable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

public class MultipartFileUtil extends FileUtil {

    public static MultipartFile toMultipartFile(File file) {
        FileInputStream fileInputStream = null;
        MultipartFile multipartFile = null;
        try {
            fileInputStream = new FileInputStream(file);
            multipartFile = new MockMultipartFile(file.getName(), file.getName(), null, fileInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return multipartFile;
    }

    public static MultipartFile toMultipartFile(Binary binary, String originalName, String binaryName) {
        MockMultipartFile mock = new MockMultipartFile(binaryName, originalName, ContentType.APPLICATION_OCTET_STREAM.toString(), binary.getData());
        return mock;
    }

    public static File toFile(MultipartFile multipartFile) {
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(multipartFile.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}
