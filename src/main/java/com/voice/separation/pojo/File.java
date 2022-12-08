package com.voice.separation.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * <p>
 * 文件管理表
 * </p>
 *
 * @author Gold_Jack
 * @since 2022-12-07
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("file")
@ApiModel(value = "File对象", description = "文件管理表")
public class File implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("文件ID")
    @TableId(value = "file_id", type = IdType.AUTO)
    private Integer fileId;

    @ApiModelProperty("文件MD5码")
    @TableField("file_md5")
    private String fileMd5;

    @ApiModelProperty("文件名")
    @TableField("filename")
    private String filename;

    @ApiModelProperty("文件归属人")
    @TableField("file_owner")
    private String fileOwner;

    @ApiModelProperty("文件是否被下载")
    @TableField("is_downloaded")
    private Integer isDownloaded;

    @ApiModelProperty("文件标签")
    @TableField("file_tag")
    private String fileTag;

    @ApiModelProperty("文件大小")
    @TableField("file_size")
    private Long fileSize;

    @ApiModelProperty("文件下载url")
    @TableField("download_url")
    private String downloadUrl;

    @ApiModelProperty("文件uuid")
    @TableField("file_uuid")
    private String fileUuid;

    @ApiModelProperty("逻辑删除")
    @TableField("is_deleted")
    private Integer isDeleted;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty("更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
