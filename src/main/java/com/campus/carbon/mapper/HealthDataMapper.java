package com.campus.carbon.mapper;

import com.campus.carbon.model.HealthData;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface HealthDataMapper {

    @Select("SELECT * FROM health_data WHERE user_id = #{userId} ORDER BY record_date DESC")
    List<HealthData> selectByUserId(@Param("userId") String userId);

    @Select("SELECT * FROM health_data WHERE id = #{id}")
    HealthData selectById(@Param("id") Long id);

    @Select("SELECT * FROM health_data WHERE user_id = #{userId} AND record_date = #{recordDate}")
    HealthData selectByUserIdAndDate(@Param("userId") String userId, @Param("recordDate") java.util.Date recordDate);

    @Insert("INSERT INTO health_data(user_id, height, weight, bmi, blood_pressure, heart_rate, blood_sugar, record_date, remark) " +
            "VALUES(#{userId}, #{height}, #{weight}, #{bmi}, #{bloodPressure}, #{heartRate}, #{bloodSugar}, #{recordDate}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(HealthData healthData);

    @Update("UPDATE health_data SET height=#{height}, weight=#{weight}, bmi=#{bmi}, blood_pressure=#{bloodPressure}, " +
            "heart_rate=#{heartRate}, blood_sugar=#{bloodSugar}, remark=#{remark} WHERE id=#{id}")
    int update(HealthData healthData);

    @Delete("DELETE FROM health_data WHERE id=#{id}")
    int delete(@Param("id") Long id);
}
