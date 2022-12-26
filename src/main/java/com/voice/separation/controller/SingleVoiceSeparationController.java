package com.voice.separation.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
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

    @Value("${path.asteroid.mix_dir}")
    private String ASTEROID_PATH;

    @Autowired
    private FileController fileController;

    @ApiOperation("默认-单音频分离，采用DPRNN的libri1Mix模型")
    @PostMapping("default")
    public R<String> voiceNoiseSeparation(@RequestParam(defaultValue = "2") Integer userId,
                                          @RequestPart(value = "file") MultipartFile audioWithNoise)
            throws IOException, InterruptedException {
        // 扩展名，这里的扩展名没有.，即a.txt的extName是txt
        String audioWithNoiseExtendedName = FileUtil.extName(audioWithNoise.getOriginalFilename());

        // 上传原语音文件至数据库
        fileController.uploadFile(userId, audioWithNoise);

        // 根据svoice的输入标准，这里把其他格式的音频全部转为.wav格式的音频文件
        assert audioWithNoiseExtendedName != null;
        if (!StrUtil.equals(audioWithNoiseExtendedName.toLowerCase(), "wav")) {
            try {
                audioWithNoise = AudioUtil.toWav(audioWithNoise);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            audioWithNoiseExtendedName = "wav";
        }

        //  源文件名、源文件名（无扩展名）
        String audioWithNoiseOriginalName = audioWithNoise.getOriginalFilename();
        String audioWithNoiseFilename = FileUtil.getPrefix(audioWithNoiseOriginalName);

        java.io.File asteroid_dir = new java.io.File(ASTEROID_PATH.replace("\\", "/") + "/" + audioWithNoiseOriginalName);
        if (!asteroid_dir.getParentFile().exists()) {
            asteroid_dir.getParentFile().mkdirs();
        }
        // 复制语音文件到分离文件夹
        audioWithNoise.transferTo(asteroid_dir);

        String[] singleSeparateCmd = {"cmd", "/c", "E:\\IDEA-Workspace\\voice-separation-backend\\asteroid\\single-separate.bat ", audioWithNoiseOriginalName};
        Process process = Runtime.getRuntime().exec(singleSeparateCmd);
        process.waitFor();

        String onlyVoiceWav = "";
        try (Stream<Path> paths = Files.walk(Paths.get(ASTEROID_PATH))){
            List<Path> fileNames = paths
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
            for (Path fileName : fileNames) {   // 正常来讲，这里只会循环一次，只产生一个清澈单人声的.wav文件
                assert audioWithNoiseFilename != null;
                if (fileName.toString().equals(audioWithNoiseFilename + "_est1.wav")) {
//                    System.out.println(fileName.toString());
                    onlyVoiceWav = (String) fileController.uploadFile(userId,
                            MultipartFileUtil.toMultipartFile(new File(fileName.toUri()))).getData();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(onlyVoiceWav);
    }
}
