package com.voice.separation.util;

import cn.hutool.core.io.FileUtil;
import com.voice.separation.repository.AudioFileRepository;
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

import static com.voice.separation.util.ResponseCode.CODE_220;

@Service
@Primary
public class SeparateUtil {

    @Autowired
    private AudioFileRepository audioFileRepository;

    private static final List<String> ALLOWED_EXT_NAME = new ArrayList<>(Arrays.asList("wav", "always_blank"));


    public static List<URI> basicSeparate(MultipartFile audio, Integer sourceNum, String mixPath, String separatePath,
                                      String scriptPath, String separateFileIdentifier) throws IOException, InterruptedException {
        String originalName = audio.getOriginalFilename();
        String audioExtName = FileUtil.extName(originalName);

        // 根据svoice的输入标准，这里把其他格式的音频全部转为.wav格式的音频文件
        assert audioExtName != null;
        if ( !ALLOWED_EXT_NAME.contains(audioExtName.toLowerCase()) ) {
            audio = AudioUtil.toWav(audio);
            audioExtName = "wav";
        }

        File mix_dir = new File(mixPath, originalName);
        if (!mix_dir.getParentFile().exists()) {
            mix_dir.getParentFile().mkdirs();
        }
        // 复制语音文件到分离文件夹
//        audio.transferTo(mix_dir);
        FileUtil.copy(MultipartFileUtil.toFile(audio), mix_dir, true);

        // 执行分离
        String[] separateCmd = {"cmd", "/c", scriptPath};
        Process process = Runtime.getRuntime().exec(separateCmd);
        process.waitFor();

        // 使用Files.walk遍历separate_dir下的所有被分离的文件
        /*
         * 由于svoice默认分离的文件样式为
         * - sourceFile.wav
         * - sourceFile_s1.wav
         * - sourceFile_s2.wav
         * 所以在此认为，所有separate_dir下，文件名包含"sourceFile_s*"的均为分离后文件，也包含源文件
         * */
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

    public static boolean removeDirFile(String filePath) {
        java.io.File toRemoveFile = new java.io.File(filePath);
        return toRemoveFile.delete();       // 是否删除成功
    }

    public static boolean removeDirFile(List<String> filePaths) {
        for (String filePath : filePaths) {
            if (!removeDirFile(filePath))
                return false;
        }
        return true;
    }

    public static boolean removeDirAll(String dirPath) {
        java.io.File dir = new java.io.File(dirPath);
        if (!dir.exists()) {
            System.err.println(dirPath + "does not exist! removeDirAll() failed.");
            return false;
        }

        String[] content = dir.list();
        assert content != null;
        for (String name : content) {
            java.io.File tempFile = new java.io.File(dirPath, name);
            if (tempFile.isDirectory()) {
                removeDirAll(tempFile.getAbsolutePath());
                tempFile.delete();
            } else {
                if (!tempFile.delete()) {
                    System.err.println(tempFile + "delete failed! <- removeDirAll().");
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean checkResults(List<URI> results, int numSrc, Class<?> fromClass) {
        if (results.size() < numSrc) {
            System.err.println(fromClass.getName() + ":(wrote by Jack)");
            System.err.println("\tSeparate result files(count: " + results.size() + ") are insufficient, please check again. Separation FAILED!!!");
            return false;
        }
        // TODO: 检查results文件的合法性
        return true;
    }
}
