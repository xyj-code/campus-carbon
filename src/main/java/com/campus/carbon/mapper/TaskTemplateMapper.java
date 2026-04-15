package com.campus.carbon.mapper;

import com.campus.carbon.model.TaskTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

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
}
