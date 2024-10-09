package com.itsthatjun.ecommerce.dto.cms;

import com.itsthatjun.ecommerce.mbg.model.Article;
import com.itsthatjun.ecommerce.mbg.model.ArticleImage;
import com.itsthatjun.ecommerce.mbg.model.ArticleQa;
import com.itsthatjun.ecommerce.mbg.model.ArticleVideo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class AdminArticleInfo extends Article implements Serializable {

    @ApiModelProperty("")
    private List<ArticleQa> QA;

    @ApiModelProperty("")
    private List<ArticleImage> images;

    @ApiModelProperty("")
    private List<ArticleVideo> videos;
}
