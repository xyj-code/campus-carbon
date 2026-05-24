package com.campus.carbon.mapper;

import com.campus.carbon.model.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductMapper {

    @Select("SELECT id, product_code AS productCode, category_code AS categoryCode, point, stock, " +
            "status, sort_order AS sortOrder, create_time AS createTime " +
            "FROM product WHERE status = 1 ORDER BY sort_order ASC, id ASC LIMIT #{offset}, #{size}")
    List<Product> selectAll(@Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM product WHERE status = 1")
    int countAll();

    @Select("SELECT id, product_code AS productCode, category_code AS categoryCode, point, stock, " +
            "status, sort_order AS sortOrder, create_time AS createTime " +
            "FROM product WHERE id = #{id} AND status = 1")
    Product selectById(Long id);

    @Update("UPDATE product SET stock = stock - 1 WHERE id = #{id} AND status = 1 AND stock > 0")
    int decreaseStock(Long id);
}
