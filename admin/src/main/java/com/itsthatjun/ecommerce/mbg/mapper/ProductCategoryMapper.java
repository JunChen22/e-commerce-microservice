package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.ProductCategory;
import com.itsthatjun.ecommerce.mbg.model.ProductCategoryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ProductCategoryMapper {
    long countByExample(ProductCategoryExample example);

    int deleteByExample(ProductCategoryExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ProductCategory row);

    int insertSelective(ProductCategory row);

    List<ProductCategory> selectByExample(ProductCategoryExample example);

    ProductCategory selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") ProductCategory row, @Param("example") ProductCategoryExample example);

    int updateByExample(@Param("row") ProductCategory row, @Param("example") ProductCategoryExample example);

    int updateByPrimaryKeySelective(ProductCategory row);

    int updateByPrimaryKey(ProductCategory row);
}