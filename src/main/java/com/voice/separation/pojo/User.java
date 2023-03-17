package com.voice.separation.pojo;

import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

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
@Document(collection = "audiofile")
@ApiModel(value = "User对象", description = "用户表")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("用户头像url")
    private Integer avatarUrl;

    @ApiModelProperty("用户权限")
    private String authority;

    @ApiModelProperty("加签验证token")
    private String token;

    @ApiModelProperty("逻辑删除")
    private Integer isDeleted;

    @ApiModelProperty("创建日期")
    private Date createTime;

    @ApiModelProperty("更新日期")
    private Date updateTime;
}
