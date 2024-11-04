package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.ProductPictures;
import com.itsthatjun.ecommerce.mbg.model.ProductPicturesExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ProductPicturesMapper {
    long countByExample(ProductPicturesExample example);

    int deleteByExample(ProductPicturesExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ProductPictures row);

    int insertSelective(ProductPictures row);

    List<ProductPictures> selectByExample(ProductPicturesExample example);

    ProductPictures selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") ProductPictures row, @Param("example") ProductPicturesExample example);

    int updateByExample(@Param("row") ProductPictures row, @Param("example") ProductPicturesExample example);

    int updateByPrimaryKeySelective(ProductPictures row);

    int updateByPrimaryKey(ProductPictures row);
}