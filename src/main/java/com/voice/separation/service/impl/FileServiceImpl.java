package com.voice.separation.service.impl;

import com.voice.separation.pojo.File;
import com.voice.separation.mapper.FileMapper;
import com.voice.separation.service.IFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
}
