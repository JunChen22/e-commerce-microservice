package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.ProductAttributeType;
import com.itsthatjun.ecommerce.mbg.model.ProductAttributeTypeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ProductAttributeTypeMapper {
    long countByExample(ProductAttributeTypeExample example);

    int deleteByExample(ProductAttributeTypeExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ProductAttributeType row);

    int insertSelective(ProductAttributeType row);

    List<ProductAttributeType> selectByExample(ProductAttributeTypeExample example);

    ProductAttributeType selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") ProductAttributeType row, @Param("example") ProductAttributeTypeExample example);

    int updateByExample(@Param("row") ProductAttributeType row, @Param("example") ProductAttributeTypeExample example);

    int updateByPrimaryKeySelective(ProductAttributeType row);

    int updateByPrimaryKey(ProductAttributeType row);
}