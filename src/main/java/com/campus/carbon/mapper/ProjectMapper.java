package com.campus.carbon.mapper;

import com.campus.carbon.model.Project;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProjectMapper {

    @Select("SELECT id, name, type, location, description, progress, images, " +
            "required_carbon AS requiredCarbon, status, create_time AS createTime " +
            "FROM project WHERE status = 1 ORDER BY required_carbon ASC")
    List<Project> selectAll();

    @Select("SELECT id, name, type, location, description, progress, images, " +
            "required_carbon AS requiredCarbon, status, create_time AS createTime " +
            "FROM project WHERE id = #{id}")
    Project selectById(Integer id);
}
