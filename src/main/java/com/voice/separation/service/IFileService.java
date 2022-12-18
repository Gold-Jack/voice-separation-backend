package com.voice.separation.service;

import com.voice.separation.pojo.File;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 文件管理表 服务类
 * </p>
 *
 * @author Gold_Jack
 * @since 2022-12-07
 */
public interface IFileService extends IService<File> {
    public String getDownloadUrl(int fileId);
    public String getFileOwner(int fileId);
    public String getFileOwner(String fileUuid);
    public File getFileByMd5(String fileMd5);
    public List<File> getUserFiles(String username);
}
