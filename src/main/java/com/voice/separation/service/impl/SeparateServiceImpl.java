package com.voice.separation.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.voice.separation.service.IFileService;
import com.voice.separation.service.ISeparateService;
import com.voice.separation.util.AudioUtil;
import com.voice.separation.util.MultipartFileUtil;
import com.voice.separation.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Primary
public class SeparateServiceImpl implements ISeparateService {

    @Autowired
    private IFileService fileService;

    private final List<String> ALLOWED_EXT_NAME = new ArrayList<>(Arrays.asList("wav", "always_blank"));

    @Override
    public List<URI> basicSeparate(MultipartFile audio, Integer sourceNum, String mixPath, String separatePath,
                                      String scriptPath, String separateFileIdentifier) throws IOException, InterruptedException {
        String originalName = audio.getOriginalFilename();
        String audioExtName = FileUtil.extName(originalName);

        // 根据svoice的输入标准，这里把其他格式的音频全部转为.wav格式的音频文件
        assert audioExtName != null;
        if ( !ALLOWED_EXT_NAME.contains(audioExtName.toLowerCase()) ) {
            audio = AudioUtil.toWav(audio);
            audioExtName = "wav";
        }

        mixPath = mixPath.replace("\\", "/");
        java.io.File mix_dir = new java.io.File(mixPath, originalName);
        if (!mix_dir.getParentFile().exists()) {
            mix_dir.getParentFile().mkdirs();
        }
        // 复制语音文件到分离文件夹
        audio.transferTo(mix_dir);

        // 执行分离
        String[] separateCmd = {"cmd", "/c", scriptPath};
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
        java.io.File separate_dir = new java.io.File(separatePath);
        String prefixAudioName = FileUtil.getPrefix(originalName);
        List<URI> results = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(separatePath))) {
            List<Path> filenames = paths
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
            for (Path filename : filenames) {
                assert prefixAudioName != null;
                // 找出包含分离后特定标识符的文件(如："xxx_s1", "xxx_s2"，标识符为"_s")，标识符由用户指定
                if (filename.toString().contains(prefixAudioName + separateFileIdentifier)) {
                    results.add(filename.toUri());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return results;
    }
}
