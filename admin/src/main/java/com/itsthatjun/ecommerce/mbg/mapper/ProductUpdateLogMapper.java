package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.ProductUpdateLog;
import com.itsthatjun.ecommerce.mbg.model.ProductUpdateLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ProductUpdateLogMapper {
    long countByExample(ProductUpdateLogExample example);

    int deleteByExample(ProductUpdateLogExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ProductUpdateLog row);

    int insertSelective(ProductUpdateLog row);

    List<ProductUpdateLog> selectByExample(ProductUpdateLogExample example);

    ProductUpdateLog selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") ProductUpdateLog row, @Param("example") ProductUpdateLogExample example);

    int updateByExample(@Param("row") ProductUpdateLog row, @Param("example") ProductUpdateLogExample example);

    int updateByPrimaryKeySelective(ProductUpdateLog row);

    int updateByPrimaryKey(ProductUpdateLog row);
}