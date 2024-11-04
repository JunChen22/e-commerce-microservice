package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.EmailTemplatesHistory;
import com.itsthatjun.ecommerce.mbg.model.EmailTemplatesHistoryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface EmailTemplatesHistoryMapper {
    long countByExample(EmailTemplatesHistoryExample example);

    int deleteByExample(EmailTemplatesHistoryExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(EmailTemplatesHistory row);

    int insertSelective(EmailTemplatesHistory row);

    List<EmailTemplatesHistory> selectByExample(EmailTemplatesHistoryExample example);

    EmailTemplatesHistory selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") EmailTemplatesHistory row, @Param("example") EmailTemplatesHistoryExample example);

    int updateByExample(@Param("row") EmailTemplatesHistory row, @Param("example") EmailTemplatesHistoryExample example);

    int updateByPrimaryKeySelective(EmailTemplatesHistory row);

    int updateByPrimaryKey(EmailTemplatesHistory row);
}