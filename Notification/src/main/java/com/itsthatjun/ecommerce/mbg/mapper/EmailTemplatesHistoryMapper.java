package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.EmailTemplatesHistory;
import com.itsthatjun.ecommerce.mbg.model.EmailTemplatesHistoryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface EmailTemplatesHistoryMapper {
    long countByExample(EmailTemplatesHistoryExample example);

    int deleteByExample(EmailTemplatesHistoryExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(EmailTemplatesHistory record);

    int insertSelective(EmailTemplatesHistory record);

    List<EmailTemplatesHistory> selectByExample(EmailTemplatesHistoryExample example);

    EmailTemplatesHistory selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") EmailTemplatesHistory record, @Param("example") EmailTemplatesHistoryExample example);

    int updateByExample(@Param("record") EmailTemplatesHistory record, @Param("example") EmailTemplatesHistoryExample example);

    int updateByPrimaryKeySelective(EmailTemplatesHistory record);

    int updateByPrimaryKey(EmailTemplatesHistory record);
}