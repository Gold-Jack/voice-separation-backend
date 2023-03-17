package com.voice.separation.controller;

import com.voice.separation.pojo.AudioFile;
import com.voice.separation.repository.AudioFileRepository;
import com.voice.separation.util.MultipartFileUtil;
import com.voice.separation.util.R;
import com.voice.separation.util.SeparateUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.voice.separation.util.ResponseCode.CODE_103;
import static com.voice.separation.util.ResponseCode.CODE_210;

@RestController
@RequestMapping("/single-voice/separate")
public class SingleVoiceSeparationController {

    private static final String PROJECT_PATH = System.getProperty("user.dir");
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
    private final Integer NUM_SRC = 1;

    @Autowired
    private FileController fileController;
    @Autowired
    private AudioFileRepository audioFileRepository;

    @ApiOperation("默认-单音频分离，采用DPRNN的libri1Mix模型")
    @PostMapping("/default")
    public R voiceNoiseSeparation(@RequestPart(value = "file") MultipartFile audioFile,
                                  @RequestParam(required = false) @Nullable String audioName,
                                  @RequestParam(required = false) @Nullable String owner)
            throws IOException, InterruptedException {
        // 上传原语音文件至数据库
        fileController.uploadFile(audioFile, audioName, owner);

        // 由于script在separateService.basicSeparate()中会直接通过exec("cmd -c <script>")被调用，
        // 且asteroid模型需要指定分离的源音频文件，
        // 所以需要把script后面加入源音频文件的路径
        final String filename = audioFile.getOriginalFilename();
        SCRIPT_PATH += " " + PROJECT_PATH + "\\" + MIX_DIR_PATH + "\\" + filename;

        List<URI> separateFilePaths = SeparateUtil.basicSeparate(audioFile, NUM_SRC,
                MIX_DIR_PATH, SEPARATE_DIR_PATH, SCRIPT_PATH,
                SEPARATE_FILE_IDENTIFIER);

        if (!SeparateUtil.checkResults(separateFilePaths, NUM_SRC, this.getClass())) {
            return R.error(CODE_210, CODE_210.getCodeMessage());
        }

        // 分离后的音频集
        List<String> urls = new ArrayList<>();

        for (URI filePath : separateFilePaths) {
            File tempFile = new File(filePath);
            String downloadUrl = (String) fileController.uploadFile(MultipartFileUtil.toMultipartFile(tempFile), audioName, FileController.DEFAULT_OWNER).getData();
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
            return R.error(CODE_210, CODE_210.getCodeMessage());
        return voiceNoiseSeparation(toSeparateFile, toSeparateFile.getOriginalFilename(), FileController.DEFAULT_OWNER);
    }
}
