package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.model.ReturnReasonPictures;
import com.itsthatjun.ecommerce.model.ReturnRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ReturnParam {

    @ApiModelProperty("")
    private ReturnRequest returnRequest;

    @ApiModelProperty("")
    private Map<String, Integer> skuQuantity;

    @ApiModelProperty("")
    private List<ReturnReasonPictures> picturesList;
}
