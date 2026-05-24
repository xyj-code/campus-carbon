package com.campus.carbon.mapper;

import com.campus.carbon.model.ExchangeRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ExchangeRecordMapper {

    @Select("SELECT id, username, product_id AS productId, product_code AS productCode, " +
            "category_code AS categoryCode, point, status, create_time AS createTime " +
            "FROM exchange_record WHERE username = #{username} ORDER BY create_time DESC LIMIT 30")
    List<ExchangeRecord> selectByUsername(String username);

    @Select("SELECT id, username, product_id AS productId, product_code AS productCode, " +
            "category_code AS categoryCode, point, status, create_time AS createTime " +
            "FROM exchange_record WHERE username = #{username} ORDER BY create_time DESC")
    List<ExchangeRecord> selectAllByUsername(String username);

    @Insert("INSERT INTO exchange_record(username, product_id, product_code, category_code, point, status) " +
            "VALUES(#{username}, #{productId}, #{productCode}, #{categoryCode}, #{point}, #{status})")
    int insert(ExchangeRecord record);
}
