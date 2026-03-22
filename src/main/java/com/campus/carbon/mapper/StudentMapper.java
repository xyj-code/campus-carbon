package com.campus.carbon.mapper;

import com.campus.carbon.model.Student;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StudentMapper {

    @Select("SELECT id, username, name, openid, points, create_time AS createTime FROM `user`")
    List<Student> selectAll();

    @Select("SELECT id, username, name, openid, points, create_time AS createTime FROM `user` WHERE id = #{id}")
    Student selectById(Long id);

    @Select("SELECT id, username, name, openid, points, create_time AS createTime FROM `user` WHERE username = #{username}")
    Student selectByUsername(String username);

    @Select("SELECT id, username, name, openid, points, create_time AS createTime FROM `user` WHERE username = #{username} AND password = #{password}")
    Student selectByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    @Insert("INSERT INTO `user`(username, name, openid, password) VALUES(#{username}, #{name}, #{openid}, #{password})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Student student);

    @Update("UPDATE `user` SET name=#{name} WHERE username=#{username}")
    int update(Student student);

    @Update("UPDATE `user` SET points = GREATEST(0, points + #{delta}) WHERE username = #{username}")
    int addPoints(@Param("username") String username, @Param("delta") int delta);

    @Delete("DELETE FROM `user` WHERE id=#{id}")
    int delete(Long id);
}
