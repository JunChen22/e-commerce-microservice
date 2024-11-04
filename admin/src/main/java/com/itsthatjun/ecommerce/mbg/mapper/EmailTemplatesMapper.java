package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.EmailTemplates;
import com.itsthatjun.ecommerce.mbg.model.EmailTemplatesExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface EmailTemplatesMapper {
    long countByExample(EmailTemplatesExample example);

    int deleteByExample(EmailTemplatesExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(EmailTemplates row);

    int insertSelective(EmailTemplates row);

    List<EmailTemplates> selectByExample(EmailTemplatesExample example);

    EmailTemplates selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") EmailTemplates row, @Param("example") EmailTemplatesExample example);

    int updateByExample(@Param("row") EmailTemplates row, @Param("example") EmailTemplatesExample example);

    int updateByPrimaryKeySelective(EmailTemplates row);

    int updateByPrimaryKey(EmailTemplates row);
}