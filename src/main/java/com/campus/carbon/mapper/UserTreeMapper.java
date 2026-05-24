package com.campus.carbon.mapper;

import com.campus.carbon.model.UserTree;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserTreeMapper {

    @Select("SELECT id, username, tree_level AS treeLevel, tree_stage AS treeStage, tree_name AS treeName, " +
            "growth_value AS growthValue, water_value AS waterValue, last_total_carbon AS lastTotalCarbon, " +
            "create_time AS createTime, update_time AS updateTime FROM user_tree WHERE username = #{username}")
    @Results(id = "userTreeMap", value = {
            @Result(column = "id", property = "id"),
            @Result(column = "username", property = "username"),
            @Result(column = "treeLevel", property = "treeLevel"),
            @Result(column = "treeStage", property = "treeStage"),
            @Result(column = "treeName", property = "treeName"),
            @Result(column = "growthValue", property = "growthValue"),
            @Result(column = "waterValue", property = "waterValue"),
            @Result(column = "lastTotalCarbon", property = "lastTotalCarbon"),
            @Result(column = "createTime", property = "createTime"),
            @Result(column = "updateTime", property = "updateTime")
    })
    UserTree selectByUsername(String username);

    @Insert("INSERT INTO user_tree(username, tree_level, tree_stage, tree_name, growth_value, water_value, last_total_carbon) " +
            "VALUES(#{username}, #{treeLevel}, #{treeStage}, #{treeName}, #{growthValue}, #{waterValue}, #{lastTotalCarbon})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserTree userTree);

    @Update("UPDATE user_tree SET tree_level = #{treeLevel}, tree_stage = #{treeStage}, tree_name = #{treeName}, " +
            "growth_value = #{growthValue}, water_value = #{waterValue}, last_total_carbon = #{lastTotalCarbon} " +
            "WHERE username = #{username}")
    int updateByUsername(UserTree userTree);
}
