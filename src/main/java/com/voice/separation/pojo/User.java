package com.voice.separation.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author Gold_Jack
 * @since 2022-12-07
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
@ApiModel(value = "User对象", description = "用户表")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    @ApiModelProperty("用户名")
    @TableField("username")
    private String username;

    @ApiModelProperty("密码")
    @TableField("password")
    private String password;

    @ApiModelProperty("用户头像url")
    @TableField("avatar_url")
    private Integer avatarUrl;

    @ApiModelProperty("用户权限")
    @TableField("authority")
    private String authority;

    @ApiModelProperty("加签验证token")
    @TableField("token")
    private String token;

    @ApiModelProperty("逻辑删除")
    @TableField("is_deleted")
    private Integer isDeleted;

    @ApiModelProperty("创建日期")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty("更新日期")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
