package com.campus.carbon.mapper;

import com.campus.carbon.model.dto.UserProjectVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserProjectMapper {

    @Select("SELECT up.id, up.project_id AS projectId, p.name AS projectName, p.type AS projectType, " +
            "up.unique_code AS uniqueCode, up.unlock_time AS unlockTime " +
            "FROM user_project up " +
            "JOIN `user` u ON up.user_id = u.id " +
            "JOIN project p ON up.project_id = p.id " +
            "WHERE u.username = #{username} ORDER BY up.unlock_time DESC")
    List<UserProjectVO> selectByUsername(String username);

    @Select("SELECT up.id, up.project_id AS projectId, p.name AS projectName, p.type AS projectType, " +
            "up.unique_code AS uniqueCode, up.unlock_time AS unlockTime " +
            "FROM user_project up " +
            "JOIN `user` u ON up.user_id = u.id " +
            "JOIN project p ON up.project_id = p.id " +
            "WHERE u.username = #{username} AND up.id = #{userProjectId}")
    UserProjectVO selectByUsernameAndId(@Param("username") String username,
                                        @Param("userProjectId") Integer userProjectId);

    @Select("SELECT COUNT(*) FROM user_project up " +
            "JOIN `user` u ON up.user_id = u.id " +
            "WHERE u.username = #{username} AND up.project_id = #{projectId}")
    int countByUsernameAndProjectId(@Param("username") String username,
                                    @Param("projectId") Integer projectId);

    @Insert("INSERT INTO user_project(user_id, project_id, unique_code, unlock_time) " +
            "VALUES((SELECT id FROM `user` WHERE username = #{username}), " +
            "#{projectId}, #{uniqueCode}, NOW())")
    int insertByUsername(@Param("username") String username,
                         @Param("projectId") Integer projectId,
                         @Param("uniqueCode") String uniqueCode);
}
