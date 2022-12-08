package com.voice.separation.mapper;

import com.voice.separation.pojo.File;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 文件管理表 Mapper 接口
 * </p>
 *
 * @author Gold_Jack
 * @since 2022-12-07
 */
public interface FileMapper extends BaseMapper<File> {

//    @Select("select download_url from voice.file where file_id=#{fileId};")
    public String getDownloadUrl(int fileId);

//    @Select("select file_owner from voice.file where file_id=#{fileId}")
    public String getFileOwner(int fileId);

    public String getFileOwnerByUuid(String fileUuid);

//    @Select("select * from voice.file where file_md5=#{fileMd5}")
    public File getFileByMd5(String fileMd5);
}
