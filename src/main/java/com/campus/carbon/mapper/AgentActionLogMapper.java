package com.campus.carbon.mapper;

import com.campus.carbon.model.AgentActionLog;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AgentActionLogMapper {

    @Select("SELECT id, session_id AS sessionId, action_id AS actionId, task_code AS taskCode, " +
            "step_order AS stepOrder, title, reason, priority_tag AS priorityTag, duration_minutes AS durationMinutes, " +
            "estimated_carbon_saving AS estimatedCarbonSaving, estimated_points AS estimatedPoints, " +
            "action_text AS actionText, action_path AS actionPath, action_type AS actionType, status, " +
            "result_note AS resultNote, completion_mode AS completionMode, verification_status AS verificationStatus, " +
            "execution_hint AS executionHint, create_time AS createTime, update_time AS updateTime " +
            "FROM agent_action_log WHERE session_id = #{sessionId} ORDER BY step_order ASC, id ASC")
    List<AgentActionLog> selectBySessionId(@Param("sessionId") String sessionId);

    @Delete("DELETE FROM agent_action_log WHERE session_id = #{sessionId}")
    int deleteBySessionId(@Param("sessionId") String sessionId);

    @Insert("INSERT INTO agent_action_log(session_id, action_id, task_code, step_order, title, reason, priority_tag, " +
            "duration_minutes, estimated_carbon_saving, estimated_points, action_text, action_path, action_type, " +
            "status, result_note, completion_mode, verification_status, execution_hint) " +
            "VALUES(#{sessionId}, #{actionId}, #{taskCode}, #{stepOrder}, #{title}, #{reason}, #{priorityTag}, " +
            "#{durationMinutes}, #{estimatedCarbonSaving}, #{estimatedPoints}, #{actionText}, #{actionPath}, #{actionType}, " +
            "#{status}, #{resultNote}, #{completionMode}, #{verificationStatus}, #{executionHint})")
    int insert(AgentActionLog log);
}
