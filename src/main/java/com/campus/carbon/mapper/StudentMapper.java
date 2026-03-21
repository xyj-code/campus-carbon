package com.campus.carbon.mapper;

import com.campus.carbon.model.Student;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StudentMapper {

    @Select("SELECT id, stu_no AS stuNo, name, openid, create_time AS createTime FROM student")
    List<Student> selectAll();

    @Select("SELECT id, stu_no AS stuNo, name, openid, create_time AS createTime FROM student WHERE id = #{id}")
    Student selectById(Long id);

    @Select("SELECT id, stu_no AS stuNo, name, openid, create_time AS createTime FROM student WHERE stu_no = #{studentId}")
    Student selectByStudentId(String studentId);

    @Select("SELECT id, stu_no AS stuNo, name, openid, create_time AS createTime FROM student WHERE stu_no = #{stuNo} AND password = #{password}")
    Student selectByStuNoAndPassword(@Param("stuNo") String stuNo, @Param("password") String password);

    @Insert("INSERT INTO student(stu_no, name, openid, password) VALUES(#{stuNo}, #{name}, #{openid}, #{password})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Student student);

    @Update("UPDATE student SET name=#{name} WHERE stu_no=#{stuNo}")
    int update(Student student);

    @Delete("DELETE FROM student WHERE id=#{id}")
    int delete(Long id);
}
