package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.dto.model.ArticleDTO;
import com.itsthatjun.ecommerce.dto.model.ImageDTO;
import com.itsthatjun.ecommerce.dto.model.QaDTO;
import com.itsthatjun.ecommerce.dto.model.VideoDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ArticleInfo extends ArticleDTO {

    @ApiModelProperty("")
    private List<QaDTO> QA;

    @ApiModelProperty("")
    private List<ImageDTO> images;

    @ApiModelProperty("")
    private List<VideoDTO> videos;
}
