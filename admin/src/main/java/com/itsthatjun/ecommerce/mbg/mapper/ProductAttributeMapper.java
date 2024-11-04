package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.ProductAttribute;
import com.itsthatjun.ecommerce.mbg.model.ProductAttributeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ProductAttributeMapper {
    long countByExample(ProductAttributeExample example);

    int deleteByExample(ProductAttributeExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ProductAttribute row);

    int insertSelective(ProductAttribute row);

    List<ProductAttribute> selectByExample(ProductAttributeExample example);

    ProductAttribute selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") ProductAttribute row, @Param("example") ProductAttributeExample example);

    int updateByExample(@Param("row") ProductAttribute row, @Param("example") ProductAttributeExample example);

    int updateByPrimaryKeySelective(ProductAttribute row);

    int updateByPrimaryKey(ProductAttribute row);
}