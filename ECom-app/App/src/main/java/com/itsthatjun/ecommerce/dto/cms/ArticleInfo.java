package com.itsthatjun.ecommerce.dto.cms;

import com.itsthatjun.ecommerce.dto.cms.outgoing.ArticleDTO;
import com.itsthatjun.ecommerce.dto.cms.outgoing.ImageDTO;
import com.itsthatjun.ecommerce.dto.cms.outgoing.QaDTO;
import com.itsthatjun.ecommerce.dto.cms.outgoing.VideoDTO;
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
