package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.EmailTemplates;
import com.itsthatjun.ecommerce.mbg.model.EmailTemplatesExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface EmailTemplatesMapper {
    long countByExample(EmailTemplatesExample example);

    int deleteByExample(EmailTemplatesExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(EmailTemplates record);

    int insertSelective(EmailTemplates record);

    List<EmailTemplates> selectByExample(EmailTemplatesExample example);

    EmailTemplates selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") EmailTemplates record, @Param("example") EmailTemplatesExample example);

    int updateByExample(@Param("record") EmailTemplates record, @Param("example") EmailTemplatesExample example);

    int updateByPrimaryKeySelective(EmailTemplates record);

    int updateByPrimaryKey(EmailTemplates record);
}