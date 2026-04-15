package com.campus.carbon.mapper;

import com.campus.carbon.model.UserTask;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserTaskMapper {

    @Select("SELECT id, username, task_code AS taskCode, period_type AS periodType, period_key AS periodKey, " +
            "current_value AS currentValue, target_value AS targetValue, reward_points AS rewardPoints, " +
            "completed, reward_status AS rewardStatus, completed_time AS completedTime, reward_time AS rewardTime, " +
            "create_time AS createTime, update_time AS updateTime " +
            "FROM user_task WHERE username = #{username} AND task_code = #{taskCode} AND period_key = #{periodKey}")
    UserTask selectOne(@Param("username") String username,
                       @Param("taskCode") String taskCode,
                       @Param("periodKey") String periodKey);

    @Insert("INSERT INTO user_task(username, task_code, period_type, period_key, current_value, target_value, " +
            "reward_points, completed, reward_status, completed_time, reward_time) " +
            "VALUES(#{username}, #{taskCode}, #{periodType}, #{periodKey}, #{currentValue}, #{targetValue}, " +
            "#{rewardPoints}, #{completed}, #{rewardStatus}, #{completedTime}, #{rewardTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserTask userTask);

    @Update("UPDATE user_task SET current_value = #{currentValue}, target_value = #{targetValue}, " +
            "reward_points = #{rewardPoints}, completed = #{completed}, reward_status = #{rewardStatus}, " +
            "completed_time = #{completedTime}, reward_time = #{rewardTime} WHERE id = #{id}")
    int update(UserTask userTask);
}
