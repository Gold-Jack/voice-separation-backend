package com.voice.separation.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;

public interface ISeparateService {

    public List<URI> basicSeparate(MultipartFile audio, Integer sourceNum, String mixDir, String SeparateDir,
                                   String scriptPath, String separateFileIdentifier) throws IOException, InterruptedException;
}
