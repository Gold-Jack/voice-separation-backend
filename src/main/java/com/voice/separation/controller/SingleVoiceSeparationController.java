package com.voice.separation.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.voice.separation.service.IFileService;
import com.voice.separation.service.ISeparateService;
import com.voice.separation.util.AudioUtil;
import com.voice.separation.util.MultipartFileUtil;
import com.voice.separation.util.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/single-voice/separate/")
public class SingleVoiceSeparationController {

    @Value("${path.asteroid.self}")
    private String ASTEROID_PATH;
    @Value("${path.asteroid.mix_dir}")
    private String MIX_DIR_PATH;
    @Value("${path.asteroid.separate_dir}")
    private String SEPARATE_DIR_PATH;
    @Value("${path.asteroid.separate_script}")
    private String SCRIPT_PATH;
    @Value("${path.asteroid.separate_file_identifier}")
    private String SEPARATE_FILE_IDENTIFIER;

    @Autowired
    private FileController fileController;
    @Autowired
    private ISeparateService separateService;
    @Autowired
    private IFileService fileService;

    @ApiOperation("默认-单音频分离，采用DPRNN的libri1Mix模型")
    @PostMapping("default")
    public R<List<String>> voiceNoiseSeparation(@RequestParam(defaultValue = "2") Integer userId,
                                          @RequestPart(value = "file") MultipartFile audioWithNoise)
            throws IOException, InterruptedException {
        final Integer num_src = 1;

        // 上传原语音文件至数据库
        fileController.uploadFile(userId, audioWithNoise);

        List<URI> separateFilePaths = separateService.basicSeparate(audioWithNoise, num_src,
                MIX_DIR_PATH, SEPARATE_DIR_PATH, SCRIPT_PATH,
                SEPARATE_FILE_IDENTIFIER);

        assert separateFilePaths != null;
        if (separateFilePaths.size() != 1)
            System.err.println("Something might go wrong in " + SingleVoiceSeparationController.class.getName() +
                    ". Because after single separation comes multi-voice results");

        // 分离后的音频集
        List<String> urls = new ArrayList<>();

        for (URI filePath : separateFilePaths) {
            File tempFile = new File(filePath);
            String downloadUrl = (String) fileController.uploadFile(userId, MultipartFileUtil.toMultipartFile(tempFile)).getData();
            urls.add(downloadUrl);
        }

        // 分离完成，删除mix_dir和separate_dir中的所有文件
        fileService.removeDirAll(MIX_DIR_PATH);
        fileService.removeDirAll(SEPARATE_DIR_PATH);

        return R.success(urls);
    }
}
