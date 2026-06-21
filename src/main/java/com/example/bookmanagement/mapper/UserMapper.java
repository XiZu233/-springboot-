package com.example.bookmanagement.mapper;

import com.example.bookmanagement.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    @Insert("INSERT INTO users(username, password, role, enabled) VALUES(#{username}, #{password}, #{role}, #{enabled})")
    int insert(User user);
}
