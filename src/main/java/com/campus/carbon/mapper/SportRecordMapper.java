package com.campus.carbon.mapper;

import com.campus.carbon.model.SportRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SportRecordMapper {

    @Select("SELECT sr.id, s.stu_no AS studentId, sr.sport_type AS sportType, sr.start_time AS startTime, " +
            "sr.end_time AS endTime, sr.distance AS amount " +
            "FROM sport_record sr JOIN student s ON sr.student_id = s.id")
    @Results(id = "sportRecordMap", value = {
            @Result(column = "id", property = "id"),
            @Result(column = "studentId", property = "studentId"),
            @Result(column = "sportType", property = "sportType"),
            @Result(column = "startTime", property = "startTime"),
            @Result(column = "endTime", property = "endTime"),
            @Result(column = "amount", property = "amount")
    })
    List<SportRecord> selectAll();

    @Select("SELECT sr.id, s.stu_no AS studentId, sr.sport_type AS sportType, sr.start_time AS startTime, " +
            "sr.end_time AS endTime, sr.distance AS amount " +
            "FROM sport_record sr JOIN student s ON sr.student_id = s.id WHERE sr.id = #{id}")
    @ResultMap("sportRecordMap")
    SportRecord selectById(Long id);

    @Select("SELECT sr.id, s.stu_no AS studentId, sr.sport_type AS sportType, sr.start_time AS startTime, " +
            "sr.end_time AS endTime, sr.distance AS amount " +
            "FROM sport_record sr JOIN student s ON sr.student_id = s.id WHERE s.stu_no = #{studentId} ORDER BY sr.start_time DESC")
    @ResultMap("sportRecordMap")
    List<SportRecord> selectByStudentId(String studentId);

    @Insert("INSERT INTO sport_record(student_id, sport_type, start_time, end_time, distance, calorie) " +
            "VALUES((SELECT id FROM student WHERE stu_no = #{studentId}), #{sportType}, #{startTime}, #{endTime}, #{amount}, #{amount})")
    int insert(SportRecord sportRecord);

    @Update("UPDATE sport_record SET sport_type=#{sportType}, start_time=#{startTime}, end_time=#{endTime}, distance=#{amount}, calorie=#{amount} WHERE id=#{id}")
    int update(SportRecord sportRecord);

    @Delete("DELETE FROM sport_record WHERE id=#{id}")
    int delete(Long id);
}
