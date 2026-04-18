package com.campus.carbon.mapper;

import com.campus.carbon.model.ActivityTopic;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface ActivityTopicMapper {

    @Select("SELECT id, activity_code AS activityCode, title, subtitle, description, banner_title AS bannerTitle, " +
            "banner_subtitle AS bannerSubtitle, cover_emoji AS coverEmoji, theme_color AS themeColor, " +
            "reward_text AS rewardText, target_text AS targetText, task_codes AS taskCodes, status, featured, sort_order AS sortOrder, " +
            "start_time AS startTime, end_time AS endTime, create_time AS createTime " +
            "FROM activity_topic ORDER BY featured DESC, sort_order ASC, id ASC")
    @Results(id = "activityTopicMap", value = {
            @Result(column = "id", property = "id"),
            @Result(column = "activityCode", property = "activityCode"),
            @Result(column = "title", property = "title"),
            @Result(column = "subtitle", property = "subtitle"),
            @Result(column = "description", property = "description"),
            @Result(column = "bannerTitle", property = "bannerTitle"),
            @Result(column = "bannerSubtitle", property = "bannerSubtitle"),
            @Result(column = "coverEmoji", property = "coverEmoji"),
            @Result(column = "themeColor", property = "themeColor"),
            @Result(column = "rewardText", property = "rewardText"),
            @Result(column = "targetText", property = "targetText"),
            @Result(column = "taskCodes", property = "taskCodes"),
            @Result(column = "status", property = "status"),
            @Result(column = "featured", property = "featured"),
            @Result(column = "sortOrder", property = "sortOrder"),
            @Result(column = "startTime", property = "startTime"),
            @Result(column = "endTime", property = "endTime"),
            @Result(column = "createTime", property = "createTime")
    })
    List<ActivityTopic> selectAll();

    @Select("SELECT id, activity_code AS activityCode, title, subtitle, description, banner_title AS bannerTitle, " +
            "banner_subtitle AS bannerSubtitle, cover_emoji AS coverEmoji, theme_color AS themeColor, " +
            "reward_text AS rewardText, target_text AS targetText, task_codes AS taskCodes, status, featured, sort_order AS sortOrder, " +
            "start_time AS startTime, end_time AS endTime, create_time AS createTime " +
            "FROM activity_topic WHERE status = 1 AND (start_time IS NULL OR start_time <= #{now}) " +
            "AND (end_time IS NULL OR end_time >= #{now}) ORDER BY featured DESC, sort_order ASC, id ASC")
    @ResultMap("activityTopicMap")
    List<ActivityTopic> selectActive(@Param("now") Date now);

    @Select("SELECT id, activity_code AS activityCode, title, subtitle, description, banner_title AS bannerTitle, " +
            "banner_subtitle AS bannerSubtitle, cover_emoji AS coverEmoji, theme_color AS themeColor, " +
            "reward_text AS rewardText, target_text AS targetText, task_codes AS taskCodes, status, featured, sort_order AS sortOrder, " +
            "start_time AS startTime, end_time AS endTime, create_time AS createTime " +
            "FROM activity_topic WHERE id = #{id}")
    @ResultMap("activityTopicMap")
    ActivityTopic selectById(Long id);

    @Insert("INSERT INTO activity_topic(activity_code, title, subtitle, description, banner_title, banner_subtitle, " +
            "cover_emoji, theme_color, reward_text, target_text, task_codes, status, featured, sort_order, start_time, end_time) " +
            "VALUES(#{activityCode}, #{title}, #{subtitle}, #{description}, #{bannerTitle}, #{bannerSubtitle}, " +
            "#{coverEmoji}, #{themeColor}, #{rewardText}, #{targetText}, #{taskCodes}, #{status}, #{featured}, #{sortOrder}, #{startTime}, #{endTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ActivityTopic activityTopic);

    @Update("UPDATE activity_topic SET activity_code = #{activityCode}, title = #{title}, subtitle = #{subtitle}, " +
            "description = #{description}, banner_title = #{bannerTitle}, banner_subtitle = #{bannerSubtitle}, " +
            "cover_emoji = #{coverEmoji}, theme_color = #{themeColor}, reward_text = #{rewardText}, target_text = #{targetText}, task_codes = #{taskCodes}, " +
            "status = #{status}, featured = #{featured}, sort_order = #{sortOrder}, start_time = #{startTime}, end_time = #{endTime} " +
            "WHERE id = #{id}")
    int update(ActivityTopic activityTopic);

    @Delete("DELETE FROM activity_topic WHERE id = #{id}")
    int delete(Long id);
}
