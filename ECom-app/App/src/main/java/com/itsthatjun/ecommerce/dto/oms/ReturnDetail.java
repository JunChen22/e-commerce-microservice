package com.itsthatjun.ecommerce.dto.oms;

import com.itsthatjun.ecommerce.dto.oms.model.ReturnItemDTO;
import com.itsthatjun.ecommerce.dto.oms.model.ReturnLogDTO;
import com.itsthatjun.ecommerce.dto.oms.model.ReturnPictureDTO;
import com.itsthatjun.ecommerce.dto.oms.model.ReturnRequestDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ReturnDetail extends ReturnRequestDTO {

    @ApiModelProperty("")
    private List<ReturnItemDTO> returnItemList;

    @ApiModelProperty("")
    private List<ReturnPictureDTO> picturesList;

    @ApiModelProperty("")
    private List<ReturnLogDTO> logList;
}
