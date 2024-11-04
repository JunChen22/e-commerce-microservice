package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.ProductAttributeCategory;
import com.itsthatjun.ecommerce.mbg.model.ProductAttributeCategoryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ProductAttributeCategoryMapper {
    long countByExample(ProductAttributeCategoryExample example);

    int deleteByExample(ProductAttributeCategoryExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ProductAttributeCategory row);

    int insertSelective(ProductAttributeCategory row);

    List<ProductAttributeCategory> selectByExample(ProductAttributeCategoryExample example);

    ProductAttributeCategory selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") ProductAttributeCategory row, @Param("example") ProductAttributeCategoryExample example);

    int updateByExample(@Param("row") ProductAttributeCategory row, @Param("example") ProductAttributeCategoryExample example);

    int updateByPrimaryKeySelective(ProductAttributeCategory row);

    int updateByPrimaryKey(ProductAttributeCategory row);
}