package com.campus.carbon.mapper;

import com.campus.carbon.model.StudentSchedule;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface StudentScheduleMapper {

    @Select("SELECT * FROM student_schedule WHERE username = #{username} ORDER BY weekday ASC, start_time ASC")
    List<StudentSchedule> selectByUsername(@Param("username") String username);

    @Select("SELECT * FROM student_schedule WHERE username = #{username} AND weekday = #{weekday} ORDER BY start_time ASC")
    List<StudentSchedule> selectByUsernameAndWeekday(@Param("username") String username, @Param("weekday") Integer weekday);

    @Select("SELECT * FROM student_schedule WHERE id = #{id}")
    StudentSchedule selectById(@Param("id") Long id);

    @Select("SELECT * FROM student_schedule WHERE id = #{id} AND username = #{username}")
    StudentSchedule selectByIdAndUsername(@Param("id") Long id, @Param("username") String username);

    @Insert("INSERT INTO student_schedule(username, weekday, course_name, location, start_time, end_time, remark) " +
            "VALUES(#{username}, #{weekday}, #{courseName}, #{location}, #{startTime}, #{endTime}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(StudentSchedule schedule);

    @Update("UPDATE student_schedule SET weekday=#{weekday}, course_name=#{courseName}, location=#{location}, " +
            "start_time=#{startTime}, end_time=#{endTime}, remark=#{remark} WHERE id=#{id} AND username=#{username}")
    int update(StudentSchedule schedule);

    @Delete("DELETE FROM student_schedule WHERE id = #{id} AND username = #{username}")
    int deleteByIdAndUsername(@Param("id") Long id, @Param("username") String username);
}
