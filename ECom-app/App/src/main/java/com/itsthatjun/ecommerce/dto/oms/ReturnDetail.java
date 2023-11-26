package com.itsthatjun.ecommerce.dto.oms;

import com.itsthatjun.ecommerce.dto.oms.outgoing.ReturnItemDTO;
import com.itsthatjun.ecommerce.dto.oms.outgoing.ReturnLogDTO;
import com.itsthatjun.ecommerce.dto.oms.outgoing.ReturnPictureDTO;
import com.itsthatjun.ecommerce.dto.oms.outgoing.ReturnRequestDTO;
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
