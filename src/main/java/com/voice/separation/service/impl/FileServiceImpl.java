package com.voice.separation.service.impl;

import cn.hutool.core.util.StrUtil;
import com.voice.separation.pojo.File;
import com.voice.separation.mapper.FileMapper;
import com.voice.separation.service.IFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 文件管理表 服务实现类
 * </p>
 *
 * @author Gold_Jack
 * @since 2022-12-07
 */
@Service
@Primary
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements IFileService {

    @Resource
    private FileMapper fileMapper;

    @Override
    public String getDownloadUrl(int fileId) {
        return fileMapper.getDownloadUrl(fileId);
    }

    @Override
    public String getFileOwner(int fileId) {
        return fileMapper.getFileOwner(fileId);
    }

    @Override
    public String getFileOwner(String fileUuid) {
        return fileMapper.getFileOwnerByUuid(fileUuid);
    }

    @Override
    public File getFileByMd5(String fileMd5) {
        return fileMapper.getFileByMd5(fileMd5);
    }

    @Override
    public List<File> getUserFiles(String username) {
        List<File> files = fileMapper.getUserFiles(username);
        return files;
    }

    @Override
    public String getFilenameByUrl(String downloadUrl) {
        return fileMapper.getFilenameByUrl(downloadUrl);
    }

    @Override
    public java.io.File getFileByUrl(String fileUrl) {
        String localized_file_path = System.getProperty("user.dir") + "/localized-files/";
        Integer fileId = fileMapper.getFileIdByUrl(fileUrl);
        String filename = fileMapper.getFilenameByUrl(fileUrl);
        String fileOwner = fileMapper.getFileOwner(fileId);
        if (StrUtil.isBlank(fileOwner))
            fileOwner = "GUEST";
        String filePath = localized_file_path + fileOwner + "/" + filename;
        return new java.io.File(filePath);
    }

    @Override
    public Integer getFileIdByUuid(String fileUuid) {
        return fileMapper.getFileIdByUuid(fileUuid);
    }

    @Override
    public String getFilename(Integer fileId) {
        return fileMapper.getFilename(fileId);
    }
}
