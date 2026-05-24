package com.campus.carbon.mapper;

import com.campus.carbon.model.AgentMemory;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AgentMemoryMapper {

    @Select("SELECT id, username, last_session_id AS lastSessionId, last_user_note AS lastUserNote, " +
            "verified_completion_count AS verifiedCompletionCount, claimed_completion_count AS claimedCompletionCount, " +
            "skipped_count AS skippedCount, last_active_time AS lastActiveTime, create_time AS createTime, update_time AS updateTime " +
            "FROM agent_memory WHERE username = #{username} LIMIT 1")
    AgentMemory selectByUsername(@Param("username") String username);

    @Insert("INSERT INTO agent_memory(username, last_session_id, last_user_note, verified_completion_count, claimed_completion_count, skipped_count, last_active_time) " +
            "VALUES(#{username}, #{lastSessionId}, #{lastUserNote}, #{verifiedCompletionCount}, #{claimedCompletionCount}, #{skippedCount}, #{lastActiveTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AgentMemory memory);

    @Update("UPDATE agent_memory SET last_session_id = #{lastSessionId}, last_user_note = #{lastUserNote}, " +
            "verified_completion_count = #{verifiedCompletionCount}, claimed_completion_count = #{claimedCompletionCount}, " +
            "skipped_count = #{skippedCount}, last_active_time = #{lastActiveTime} WHERE username = #{username}")
    int updateByUsername(AgentMemory memory);
}
