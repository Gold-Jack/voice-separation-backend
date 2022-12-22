package com.voice.separation.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.voice.separation.pojo.File;
import com.voice.separation.service.IFileService;
import com.voice.separation.service.IUserService;
import com.voice.separation.util.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;

import static com.voice.separation.util.ResponseCode.CODE_302;

/**
 * <p>
 * 文件管理表 前端控制器
 * </p>
 *
 * @author Gold_Jack
 * @since 2022-12-07
 */
@RestController
@RequestMapping("/file/")
public class FileController {

    private static final String PROJECT_PATH = System.getProperty("user.dir");
    private static final String LOCAL_FILE_PREFIX = "/localized-files/";

    @Value("${project.deployment.host}")
    private String HOST;
    @Value("${server.port}")
    private String PORT;

    @Autowired
    private IFileService fileService;
    @Autowired
    private IUserService userService;

    @PostMapping(value = "upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R uploadFile( @RequestParam(value = "userId", defaultValue = "2") int userId,
            @RequestPart(value = "file") MultipartFile file) throws IOException {
        // 获取原始文件名
        String originalFileName = file.getOriginalFilename();
        String fileType = FileUtil.extName(originalFileName);
        Long fileSize = file.getSize() / 1024;  // 以kb为单位保存

        // uuid可以唯一标识文件，防止文件重名
        String uuid = IdUtil.fastSimpleUUID();
        String fileUUID = uuid + StrUtil.DOT + fileType;
        java.io.File uploadFile = new java.io.File(getFileUploadPath(userId) + originalFileName);

        // 检查文件上传路径是否为空，如果为空，新建文件夹
        if (!uploadFile.getParentFile().exists()) {
            uploadFile.getParentFile().mkdirs();
        }

        // 利用文件md5码，防止相同文件多次上传，挤占内存空间
        String md5 = SecureUtil.md5(file.getInputStream());     // 根据上传文件生成md5，getInputStream()是为了把MultipartFile转为java.util.File类型
        File dbFile = fileService.getFileByMd5(md5);           // 查询数据库中是否有相同的md5
        if (dbFile != null && StrUtil.equals(dbFile.getFileOwner(), userService.getUsernameById(userId))) {   // 数据库中已经有相同的文件
            return R.success(dbFile.getDownloadUrl());   // 直接返回该文件的downloadUrl
        }

        /*
         * 如果数据库中没有相同md5码的文件，说明当前上传的文件是新文件，则上传文件，生成url
         * */
        File toSaveFile = new File();
        // 将文件上传到指定位置
        file.transferTo(uploadFile);
        // 生成下载地址
        String downloadUrl = "http://" + HOST + ":" + PORT + "/file/download/" + fileUUID;
        /*
         * 将文件信息写入数据库
         * */

        // 设置文件的基本属性
        String fileOwner = userService.getUsernameById(userId);
        toSaveFile.setFilename(originalFileName);
//        toSaveFile.setFileType(fileType);
        toSaveFile.setFileSize(fileSize);
        toSaveFile.setFileMd5(md5);
        toSaveFile.setFileUuid(fileUUID);
        toSaveFile.setDownloadUrl(downloadUrl);
        toSaveFile.setFileOwner(fileOwner);
        // 存储文件到数据库
        fileService.save(toSaveFile);

        return R.success(downloadUrl);
    }

    @ApiOperation("下载文件（获取文件）")
    @GetMapping("download/{fileUuid}")
    public R downloadFile(@PathVariable String fileUuid,
                          HttpServletResponse response) throws IOException {
        // 根据文件唯一标识码获取文件
        Integer userId = userService.getOneByUsername(fileService.getFileOwner(fileUuid)).getUserId();
        String filename = fileService.getFilename(fileService.getFileIdByUuid(fileUuid));
        java.io.File targetFile = new java.io.File(getFileUploadPath(userId) + filename);
        // 设置输出流格式
        ServletOutputStream os = response.getOutputStream();
        response.addHeader("Content-Disposition", "attachment; filename=" + URLDecoder.decode(fileUuid, CharsetUtil.UTF_8));
        response.setContentType("application/octet-stream");

        // 读取文件的字节流
        os.write(FileUtil.readBytes(targetFile));
        os.flush();
        os.close();
        return R.success();
    }

    @Deprecated
    @ApiOperation("下载文件，直接返回文件本身")
    @GetMapping("download")
    public java.io.File directDownload(@RequestParam(value = "url") String downloadUrl) {
        java.io.File file = new java.io.File(downloadUrl);
        return file;
    }

    @ApiOperation("获取用户上传的所有文件信息")
    @GetMapping("get/user-files")
    public R getUserFiles(@RequestParam String username) {
        // 确认当前用户是存在的
        if (userService.getOneByUsername(username) == null)
            return R.error(CODE_302, CODE_302.getCodeMessage());

        List<File> userFiles = fileService.getUserFiles(username);
        return R.success(userFiles);
    }

    @ApiOperation("通过fileId获取文件的downloadUrl")
    @GetMapping("get/download-url")
    public R getDownloadUrlById(@RequestParam Integer fileId) {
        String downloadUrl = fileService.getDownloadUrl(fileId);
        return R.success(downloadUrl);
    }

    @ApiOperation("通过文件url获得文件名")
    @GetMapping("get/filename")
    public R getFilenameByUrl(@RequestParam String downloadUrl) {
        return R.success(fileService.getFilenameByUrl(downloadUrl));
    }

    @ApiOperation("用过用户id获取用户名，并拼接成文件上传路径")
    private String getFileUploadPath(Integer userId) {
        String username = userService.getUsernameById(userId);
        String fileUploadPath = PROJECT_PATH + LOCAL_FILE_PREFIX + username + "/";
        return fileUploadPath;
    }
}
