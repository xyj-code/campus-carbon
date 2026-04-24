package com.campus.carbon.mapper;

import com.campus.carbon.model.AgentSession;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AgentSessionMapper {

    @Select("SELECT id, session_id AS sessionId, username, session_status AS sessionStatus, " +
            "current_action_id AS currentActionId, user_note AS userNote, plan_version AS planVersion, " +
            "last_active_time AS lastActiveTime, create_time AS createTime, update_time AS updateTime " +
            "FROM agent_session WHERE username = #{username} ORDER BY update_time DESC LIMIT 1")
    AgentSession selectLatestByUsername(@Param("username") String username);

    @Select("SELECT id, session_id AS sessionId, username, session_status AS sessionStatus, " +
            "current_action_id AS currentActionId, user_note AS userNote, plan_version AS planVersion, " +
            "last_active_time AS lastActiveTime, create_time AS createTime, update_time AS updateTime " +
            "FROM agent_session WHERE session_id = #{sessionId} LIMIT 1")
    AgentSession selectBySessionId(@Param("sessionId") String sessionId);

    @Insert("INSERT INTO agent_session(session_id, username, session_status, current_action_id, user_note, plan_version, last_active_time) " +
            "VALUES(#{sessionId}, #{username}, #{sessionStatus}, #{currentActionId}, #{userNote}, #{planVersion}, #{lastActiveTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AgentSession session);

    @Update("UPDATE agent_session SET session_status = #{sessionStatus}, current_action_id = #{currentActionId}, " +
            "user_note = #{userNote}, plan_version = #{planVersion}, last_active_time = #{lastActiveTime} WHERE session_id = #{sessionId}")
    int updateBySessionId(AgentSession session);
}
