package com.campus.carbon.mapper;

import com.campus.carbon.model.StepCount;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface StepCountMapper {

    @Select("SELECT sc.id, u.username AS studentId, sc.date, sc.step_num AS steps, sc.calorie AS calories, sc.duration " +
            "FROM step_count sc JOIN `user` u ON sc.student_id = u.id")
    @Results(id = "stepCountMap", value = {
            @Result(column = "id", property = "id"),
            @Result(column = "studentId", property = "studentId"),
            @Result(column = "date", property = "date"),
            @Result(column = "steps", property = "steps"),
            @Result(column = "calories", property = "calories"),
            @Result(column = "duration", property = "duration")
    })
    List<StepCount> selectAll();

    @Select("SELECT sc.id, u.username AS studentId, sc.date, sc.step_num AS steps, sc.calorie AS calories, sc.duration " +
            "FROM step_count sc JOIN `user` u ON sc.student_id = u.id WHERE sc.id = #{id}")
    @ResultMap("stepCountMap")
    StepCount selectById(Long id);

    @Select("SELECT sc.id, u.username AS studentId, sc.date, sc.step_num AS steps, sc.calorie AS calories, sc.duration " +
            "FROM step_count sc JOIN `user` u ON sc.student_id = u.id WHERE u.username = #{studentId} ORDER BY sc.date DESC")
    @ResultMap("stepCountMap")
    List<StepCount> selectByStudentId(String studentId);

    @Select("SELECT sc.id, u.username AS studentId, sc.date, sc.step_num AS steps, sc.calorie AS calories, sc.duration " +
            "FROM step_count sc JOIN `user` u ON sc.student_id = u.id WHERE u.username = #{studentId} AND sc.date = #{date}")
    @ResultMap("stepCountMap")
    StepCount selectByStudentIdAndDate(@Param("studentId") String studentId, @Param("date") Date date);

    @Insert("INSERT INTO step_count(student_id, date, step_num, calorie, duration) " +
            "VALUES((SELECT id FROM `user` WHERE username = #{studentId}), #{date}, #{steps}, #{calories}, #{duration}) " +
            "ON DUPLICATE KEY UPDATE step_num=#{steps}, calorie=#{calories}, duration=#{duration}")
    int insert(StepCount stepCount);

    @Update("UPDATE step_count SET step_num=#{steps}, calorie=#{calories}, duration=#{duration} " +
            "WHERE student_id=(SELECT id FROM `user` WHERE username = #{studentId}) AND date=#{date}")
    int update(StepCount stepCount);

    @Delete("DELETE FROM step_count WHERE id=#{id}")
    int delete(Long id);
}
