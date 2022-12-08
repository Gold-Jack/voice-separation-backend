package com.voice.separation.mapper;

import com.voice.separation.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.experimental.FieldDefaults;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author Gold_Jack
 * @since 2022-12-07
 */
public interface UserMapper extends BaseMapper<User> {

//    @Select("select authority from voice.user where user_id=#{userId}")
    public String getAuthority(int userId);

//    @Select("select username from voice.user where user_id=#{userId};")
    public String getUsernameById(int userId);

    public User getOneByUsername(String username);
}
