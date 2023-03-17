package com.voice.separation.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.mongodb.client.gridfs.GridFSBucket;
import com.voice.separation.pojo.AudioFile;
import com.voice.separation.repository.AudioFileRepository;
import com.voice.separation.util.MultipartFileUtil;
import com.voice.separation.util.R;
import io.swagger.annotations.ApiOperation;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.voice.separation.util.ResponseCode.*;

/**
 * <p>
 * 文件管理表 前端控制器
 * </p>
 *
 * @author Gold_Jack
 * @since 2022-12-07
 */
@RestController
@RequestMapping("/file")
public class FileController {

    private static final String PROJECT_PATH = System.getProperty("user.dir");
    private static final String LOCAL_FILE_PREFIX = "/localized-files/";

    @Value("${project.deployment.host}")
    private String HOST;
    @Value("${server.port}")
    private String PORT;
    private String HEADER = "http://";
    private String DOWNLOAD_PATH = "/file/download/";

    public static String DEFAULT_OWNER = "GUEST";

    @Autowired
    private AudioFileRepository audioFileRepository;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private GridFSBucket gridFSBucket;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R uploadFile(@RequestPart(value = "file") MultipartFile audioFile,
                        @RequestParam(required = false) String audioName,
                        @RequestParam(required = false) String owner) throws IOException {
        if (audioName == null) {
            audioName = audioFile.getOriginalFilename();
        }
        if (owner == null) {
            owner = DEFAULT_OWNER;
        }
        Binary binary = new Binary(audioFile.getBytes());
        Optional<AudioFile> deprecatedFile = audioFileRepository.findDeprecation(audioName, owner);
        AudioFile insert;
        if (deprecatedFile.isPresent()) {
            insert = deprecatedFile.get();
            insert.setBinary(binary);
            insert.setUpdateTime(new Date(DateUtil.current()));
            audioFileRepository.save(insert);
        } else {
            insert = audioFileRepository.insert(new AudioFile(audioName, owner, binary));
        }
        String audioId = insert.getAudioId();
        String downloadUrl = encodeDownloadUrl(audioId);
        insert.setDownloadUrl(downloadUrl);

        return R.success(downloadUrl);
    }

    @ApiOperation("下载文件（获取文件）")
    @GetMapping("/download/{audioId}")
    public R downloadFile(@PathVariable String audioId,
                             HttpServletResponse response) throws IOException {
        Optional<AudioFile> audioOpt = audioFileRepository.findAudioById(audioId);
        if (!audioOpt.isPresent()) {
            return R.error(CODE_311, CODE_311.getCodeMessage());
        }
        AudioFile audioFile = audioOpt.get();
        Binary binary = audioFile.getBinary();
        if (binary == null)
            return R.error(CODE_103, CODE_103.getCodeMessage());
        String audioName = audioFile.getAudioName();

        // 设置输出流格式
        ServletOutputStream os = response.getOutputStream();
        response.addHeader("Content-Disposition", "attachment; filename=" + audioName);
        response.setContentType("application/octet-stream");

        // 读取文件的字节流
        os.write(binary.getData());
        os.flush();
        os.close();
        return R.success();
    }

    public MultipartFile getMultipartFileByUrl(String url) {
        String audioId = decodeDownloadUrl(url);
        Optional<AudioFile> audioOpt = audioFileRepository.findAudioById(audioId);
        if (!audioOpt.isPresent())
            return null;
        AudioFile audioFile = audioOpt.get();
        MultipartFile toSeparateFile = MultipartFileUtil.toMultipartFile(audioFile.getBinary(), audioFile.getAudioName(), audioFile.getAudioName());
        return toSeparateFile;
    }

    public String encodeDownloadUrl(String audioId) {
        String downloadUrl = HEADER + HOST + ":" + PORT + DOWNLOAD_PATH + audioId;
        return downloadUrl;
    }

    public String decodeDownloadUrl(String downloadUrl) {
        String[] s = downloadUrl.split("/");
        return s[s.length - 1];
    }
}
