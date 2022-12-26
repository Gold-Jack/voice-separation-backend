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
@RequestMapping("/multi-voice/separate/")
public class MultiVoiceSeparationController {

    @Value("${path.svoice.self}")
    private String SVOICE_PATH;
    @Value("${path.svoice.mix_dir}")
    private String MIX_DIR_PATH;
    @Value("${path.svoice.separate_dir}")
    private String SEPARATE_DIR_PATH;
    @Value("${path.svoice.model_checkpoint}")
    private String MODEL_CHECKPOINT_PATH;
    @Value("${path.svoice.separate_script}")
    private String SCRIPT_PATH;
    @Value("${path.svoice.separate_file_identifier}")
    private String SEPARATE_FILE_IDENTIFIER;

    @Autowired
    private FileController fileController;
    @Autowired
    private IFileService fileService;
    @Autowired
    private ISeparateService separateService;

    @ApiOperation("默认-多音频分离方法，采用svoice的模型")
    @PostMapping("default")
    public R<List<String>> separate2voice(@RequestParam(defaultValue = "2") Integer userId,
                                          @RequestPart(value = "file") MultipartFile sourceAudio)
            throws IOException, InterruptedException {
        // 当前方法是几人声分离
        final Integer num_src = 2;

        // 分离后的音频集
        List<String> urls = new ArrayList<>();

        // 上传原语音文件至数据库
        fileController.uploadFile(userId, sourceAudio);

        List<URI> separateFilePaths = separateService.basicSeparate(sourceAudio, num_src, MIX_DIR_PATH, SEPARATE_DIR_PATH,
                SCRIPT_PATH, SEPARATE_FILE_IDENTIFIER);
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

    @ApiOperation("通过已经上传至数据库的源音频文件url，对其进行分离")
    @GetMapping("by-source-audio-url")
    public R separate2VoiceByUrl(@RequestParam(defaultValue = "2") Integer userId,
                                 @RequestParam(value = "url") String sourceAudioUrl) throws IOException, InterruptedException {
        MultipartFile toSeparateFile = MultipartFileUtil.toMultipartFile(fileService.getFileByUrl(sourceAudioUrl));
        return separate2voice(userId, toSeparateFile);
    }
}
