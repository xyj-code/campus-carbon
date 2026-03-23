package com.campus.carbon.mapper;

import com.campus.carbon.model.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductMapper {

    @Select("SELECT id, name, point, stock, image, description, status, create_time AS createTime " +
            "FROM product WHERE status = 1 ORDER BY id ASC LIMIT #{offset}, #{size}")
    List<Product> selectAll(@Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM product WHERE status = 1")
    int countAll();

    @Select("SELECT id, name, point, stock, image, description, status, create_time AS createTime " +
            "FROM product WHERE id = #{id} AND status = 1")
    Product selectById(Long id);

    @Update("UPDATE product SET stock = stock - 1 WHERE id = #{id} AND stock > 0")
    int decreaseStock(Long id);
}
