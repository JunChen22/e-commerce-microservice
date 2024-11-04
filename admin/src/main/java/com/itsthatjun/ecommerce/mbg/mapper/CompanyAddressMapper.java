package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.CompanyAddress;
import com.itsthatjun.ecommerce.mbg.model.CompanyAddressExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CompanyAddressMapper {
    long countByExample(CompanyAddressExample example);

    int deleteByExample(CompanyAddressExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(CompanyAddress row);

    int insertSelective(CompanyAddress row);

    List<CompanyAddress> selectByExample(CompanyAddressExample example);

    CompanyAddress selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") CompanyAddress row, @Param("example") CompanyAddressExample example);

    int updateByExample(@Param("row") CompanyAddress row, @Param("example") CompanyAddressExample example);

    int updateByPrimaryKeySelective(CompanyAddress row);

    int updateByPrimaryKey(CompanyAddress row);
}