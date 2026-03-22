package com.campus.carbon.mapper;

import com.campus.carbon.model.PointsRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PointsRecordMapper {

    @Select("SELECT id, username, type, amount, title, remark, create_time AS createTime " +
            "FROM points_record WHERE username = #{username} " +
            "ORDER BY create_time DESC LIMIT #{offset}, #{size}")
    List<PointsRecord> selectByUsername(@Param("username") String username,
                                        @Param("offset") int offset,
                                        @Param("size") int size);

    @Select("SELECT id, username, type, amount, title, remark, create_time AS createTime " +
            "FROM points_record WHERE username = #{username} AND type = #{type} " +
            "ORDER BY create_time DESC LIMIT #{offset}, #{size}")
    List<PointsRecord> selectByUsernameAndType(@Param("username") String username,
                                               @Param("type") String type,
                                               @Param("offset") int offset,
                                               @Param("size") int size);

    @Select("SELECT COUNT(*) FROM points_record WHERE username = #{username}")
    int countByUsername(@Param("username") String username);

    @Select("SELECT COUNT(*) FROM points_record WHERE username = #{username} AND type = #{type}")
    int countByUsernameAndType(@Param("username") String username, @Param("type") String type);

    @Insert("INSERT INTO points_record(username, type, amount, title, remark) " +
            "VALUES(#{username}, #{type}, #{amount}, #{title}, #{remark})")
    int insert(PointsRecord record);
}
