package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.Email;
import com.itsthatjun.ecommerce.mbg.model.EmailExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface EmailMapper {
    long countByExample(EmailExample example);

    int deleteByExample(EmailExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Email row);

    int insertSelective(Email row);

    List<Email> selectByExample(EmailExample example);

    Email selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") Email row, @Param("example") EmailExample example);

    int updateByExample(@Param("row") Email row, @Param("example") EmailExample example);

    int updateByPrimaryKeySelective(Email row);

    int updateByPrimaryKey(Email row);
}