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
@RequestMapping("/multi-voice/separate/")
public class MultiVoiceSeparationController {

    @Value("${api.path.svoice}")
    private String SVOICE_PATH;
    @Value("${api.path.mix_dir}")
    private String MIX_DIR_PATH;
    @Value("${api.path.separate_dir}")
    private String SEPARATE_DIR_PATH;
    @Value("${api.path.model_checkpoint}")
    private String MODEL_CHECKPOINT_PATH;
    @Value("${api.path.separate_script}")
    private String SEPARATE_SCRIPT_PATH;

    @Autowired
    private FileController fileController;

    @ApiOperation("默认-多音频分离方法，采用svoice的模型")
    @PostMapping("default")
    public R<List<String>> separate2voice(@RequestParam Integer userId,
                                          @RequestPart(value = "toSeparateFile") MultipartFile sourceAudio)
            throws IOException, InterruptedException {
        // 当前方法是几人声分离
        final Integer num_src = 2;

        // 分离后的音频集
        List<String> separatedVoices = new ArrayList<>();

        // 扩展名，这里的扩展名没有.，即a.txt的extName是txt
        String sourceAudioExtendedName = FileUtil.extName(sourceAudio.getOriginalFilename());

        // 上传原语音文件至数据库
        fileController.uploadFile(userId, sourceAudio);

        // 根据svoice的输入标准，这里把其他格式的音频全部转为.wav格式的音频文件
        assert sourceAudioExtendedName != null;
        if (!StrUtil.equals(sourceAudioExtendedName.toLowerCase(), "wav")) {
            sourceAudio = AudioUtil.toWav(sourceAudio);
            sourceAudioExtendedName = "wav";
        }

        //  源文件名、源文件名（无扩展名）
        String sourceAudioOriginalName = sourceAudio.getOriginalFilename();
        String sourceAudioFilename = FileUtil.getPrefix(sourceAudioOriginalName);

        java.io.File mix_dir = new java.io.File(MIX_DIR_PATH.replace("\\", "/") + "/" + sourceAudioOriginalName);
        if (!mix_dir.getParentFile().exists()) {
            mix_dir.getParentFile().mkdirs();
        }
        // 复制语音文件到分离文件夹
        sourceAudio.transferTo(mix_dir);

        // 执行分离
        String[] separateCmd = {"cmd", "/c", SEPARATE_SCRIPT_PATH};
        Process process = Runtime.getRuntime().exec(separateCmd);
        // 下面这种直接写执行语句的方法即将被废弃(@deprecated)
        // Process process = Runtime.getRuntime().exec("cmd /c E:\\python-workspace\\svoice\\activate.bat");
        process.waitFor();

        // 使用Files.walk遍历separate_dir下的所有被分离的文件
        /*
        * 由于svoice默认分离的文件样式为
        * - sourceFile.wav
        * - sourceFile_s1.wav
        * - sourceFile_s2.wav
        * 所以在此认为，所有separate_dir下，文件名包含"sourceFile_s*"的均为分离后文件，也包含源文件
        * */
        java.io.File separate_dir = new java.io.File(SEPARATE_DIR_PATH);
        try (Stream<Path> paths = Files.walk(Paths.get(SEPARATE_DIR_PATH))){
            List<Path> fileNames = paths
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
            for (Path fileName : fileNames) {
                assert sourceAudioFilename != null;
                if (fileName.toString().contains(sourceAudioFilename + "_s")) {
//                    System.out.println(fileName.toString());
                    String downloadUrl = (String) fileController.uploadFile(userId,
                            MultipartFileUtil.toMultipartFile(new File(fileName.toUri()))).getData();
                    separatedVoices.add(downloadUrl);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(separatedVoices);
    }
}
