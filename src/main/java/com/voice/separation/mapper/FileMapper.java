package com.voice.separation.mapper;

import com.voice.separation.pojo.File;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 文件管理表 Mapper 接口
 * </p>
 *
 * @author Gold_Jack
 * @since 2022-12-07
 */
public interface FileMapper extends BaseMapper<File> {
    public String getDownloadUrl(int fileId);
    public String getFileOwner(int fileId);
    public String getFileOwnerByUuid(String fileUuid);
    public File getFileByMd5(String fileMd5);
    public List<File> getUserFiles(String username);
}
