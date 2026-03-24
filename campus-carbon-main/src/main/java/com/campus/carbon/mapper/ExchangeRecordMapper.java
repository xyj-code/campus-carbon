package com.campus.carbon.mapper;

import com.campus.carbon.model.ExchangeRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ExchangeRecordMapper {

    @Select("SELECT id, username, product_id AS productId, product_name AS productName, point, " +
            "create_time AS createTime FROM exchange_record " +
            "WHERE username = #{username} ORDER BY create_time DESC LIMIT 30")
    List<ExchangeRecord> selectByUsername(String username);

    @Insert("INSERT INTO exchange_record(username, product_id, product_name, point) " +
            "VALUES(#{username}, #{productId}, #{productName}, #{point})")
    int insert(ExchangeRecord record);
}
