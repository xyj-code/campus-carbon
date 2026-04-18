package com.campus.carbon.mapper;

import com.campus.carbon.model.TaskTemplate;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TaskTemplateMapper {

    @Select("SELECT id, task_code AS taskCode, period_type AS periodType, metric_type AS metricType, " +
            "target_value AS targetValue, reward_points AS rewardPoints, status, sort_order AS sortOrder, " +
            "create_time AS createTime FROM task_template WHERE status = 1 ORDER BY sort_order ASC, id ASC")
    @Results(id = "taskTemplateMap", value = {
            @Result(column = "id", property = "id"),
            @Result(column = "taskCode", property = "taskCode"),
            @Result(column = "periodType", property = "periodType"),
            @Result(column = "metricType", property = "metricType"),
            @Result(column = "targetValue", property = "targetValue"),
            @Result(column = "rewardPoints", property = "rewardPoints"),
            @Result(column = "status", property = "status"),
            @Result(column = "sortOrder", property = "sortOrder"),
            @Result(column = "createTime", property = "createTime")
    })
    List<TaskTemplate> selectActive();

    @Select("SELECT id, task_code AS taskCode, period_type AS periodType, metric_type AS metricType, " +
            "target_value AS targetValue, reward_points AS rewardPoints, status, sort_order AS sortOrder, " +
            "create_time AS createTime FROM task_template ORDER BY sort_order ASC, id ASC")
    @ResultMap("taskTemplateMap")
    List<TaskTemplate> selectAll();

    @Select("SELECT id, task_code AS taskCode, period_type AS periodType, metric_type AS metricType, " +
            "target_value AS targetValue, reward_points AS rewardPoints, status, sort_order AS sortOrder, " +
            "create_time AS createTime FROM task_template WHERE id = #{id}")
    @ResultMap("taskTemplateMap")
    TaskTemplate selectById(Long id);

    @Insert("INSERT INTO task_template(task_code, period_type, metric_type, target_value, reward_points, status, sort_order) " +
            "VALUES(#{taskCode}, #{periodType}, #{metricType}, #{targetValue}, #{rewardPoints}, #{status}, #{sortOrder})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TaskTemplate taskTemplate);

    @Update("UPDATE task_template SET task_code = #{taskCode}, period_type = #{periodType}, metric_type = #{metricType}, " +
            "target_value = #{targetValue}, reward_points = #{rewardPoints}, status = #{status}, sort_order = #{sortOrder} " +
            "WHERE id = #{id}")
    int update(TaskTemplate taskTemplate);
}
