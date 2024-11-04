package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.ProductExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ProductMapper {
    long countByExample(ProductExample example);

    int deleteByExample(ProductExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Product row);

    int insertSelective(Product row);

    List<Product> selectByExample(ProductExample example);

    Product selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") Product row, @Param("example") ProductExample example);

    int updateByExample(@Param("row") Product row, @Param("example") ProductExample example);

    int updateByPrimaryKeySelective(Product row);

    int updateByPrimaryKey(Product row);
}