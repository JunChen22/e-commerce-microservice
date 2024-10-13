package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.dto.model.ReturnItemDTO;
import com.itsthatjun.ecommerce.dto.model.ReturnLogDTO;
import com.itsthatjun.ecommerce.dto.model.ReturnPictureDTO;
import com.itsthatjun.ecommerce.dto.model.ReturnRequestDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReturnDetail extends ReturnRequestDTO {

    @ApiModelProperty("")
    private List<ReturnItemDTO> returnItemList;

    @ApiModelProperty("")
    private List<ReturnPictureDTO> picturesList;

    @ApiModelProperty("")
    private List<ReturnLogDTO> logList;
}
