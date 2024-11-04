package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.ProductSku;
import com.itsthatjun.ecommerce.mbg.model.ProductSkuExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ProductSkuMapper {
    long countByExample(ProductSkuExample example);

    int deleteByExample(ProductSkuExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ProductSku row);

    int insertSelective(ProductSku row);

    List<ProductSku> selectByExample(ProductSkuExample example);

    ProductSku selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") ProductSku row, @Param("example") ProductSkuExample example);

    int updateByExample(@Param("row") ProductSku row, @Param("example") ProductSkuExample example);

    int updateByPrimaryKeySelective(ProductSku row);

    int updateByPrimaryKey(ProductSku row);
}