package com.voice.separation.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.voice.separation.pojo.AudioFile;
import com.voice.separation.repository.AudioFileRepository;
import com.voice.separation.util.*;
import io.swagger.annotations.ApiOperation;
import org.bson.types.Binary;
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
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.voice.separation.util.ResponseCode.CODE_103;
import static com.voice.separation.util.ResponseCode.CODE_220;

@RestController
@RequestMapping("/multi-voice/separate")
public class MultiVoiceSeparationController {

    private static final String PROJECT_PATH = System.getProperty("user.dir");
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
    private final Integer NUM_SRC = 2;

    @Autowired
    private FileController fileController;
    @Autowired
    private AudioFileRepository audioFileRepository;

    @ApiOperation("默认-多音频分离方法，采用svoice的模型")
    @PostMapping("/default")
    public R separate2voice(@RequestPart(value = "file") MultipartFile audioFile,
                          @RequestParam(required = false) String audioName,
                          @RequestParam(required = false) String owner)
            throws IOException, InterruptedException {
        // 上传原语音文件至数据库
        fileController.uploadFile(audioFile, audioName, owner);

        // 分离后的音频集
        List<String> urls = new ArrayList<>();
        List<URI> separateFilePaths = SeparateUtil.basicSeparate(audioFile, NUM_SRC, MIX_DIR_PATH, SEPARATE_DIR_PATH,
                SCRIPT_PATH, SEPARATE_FILE_IDENTIFIER);

        if (!SeparateUtil.checkResults(separateFilePaths, NUM_SRC, this.getClass())) {
            return R.error(CODE_220, CODE_220.getCodeMessage());
        }

        for (URI filePath : separateFilePaths) {
            File tempFile = new File(filePath);
            String downloadUrl = (String) fileController.uploadFile(
                    MultipartFileUtil.toMultipartFile(tempFile), tempFile.getName(), FileController.DEFAULT_OWNER).getData();
            urls.add(downloadUrl);
        }

        // 分离完成，删除mix_dir和separate_dir中的所有文件
        SeparateUtil.removeDirAll(MIX_DIR_PATH);
        SeparateUtil.removeDirAll(SEPARATE_DIR_PATH);

        return R.success(urls);
    }

    @ApiOperation("通过已经上传至数据库的源音频文件url，对其进行分离")
    @PostMapping("/by-source-audio-url")
    public R separate2VoiceByUrl(@RequestParam(defaultValue = "2", required = false) Integer userId,
                                 @RequestParam(value = "url") String sourceAudioUrl) throws IOException, InterruptedException {
        MultipartFile toSeparateFile = fileController.getMultipartFileByUrl(sourceAudioUrl);
        if (toSeparateFile == null)
            return R.error(CODE_220, CODE_220.getCodeMessage());
        return separate2voice(toSeparateFile, toSeparateFile.getOriginalFilename(), FileController.DEFAULT_OWNER);
    }
}
