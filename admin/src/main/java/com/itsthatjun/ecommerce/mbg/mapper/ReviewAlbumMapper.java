package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.ReviewAlbum;
import com.itsthatjun.ecommerce.mbg.model.ReviewAlbumExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ReviewAlbumMapper {
    long countByExample(ReviewAlbumExample example);

    int deleteByExample(ReviewAlbumExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ReviewAlbum row);

    int insertSelective(ReviewAlbum row);

    List<ReviewAlbum> selectByExample(ReviewAlbumExample example);

    ReviewAlbum selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") ReviewAlbum row, @Param("example") ReviewAlbumExample example);

    int updateByExample(@Param("row") ReviewAlbum row, @Param("example") ReviewAlbumExample example);

    int updateByPrimaryKeySelective(ReviewAlbum row);

    int updateByPrimaryKey(ReviewAlbum row);
}