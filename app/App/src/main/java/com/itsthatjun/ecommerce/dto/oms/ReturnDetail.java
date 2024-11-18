package com.itsthatjun.ecommerce.dto.oms;

import com.itsthatjun.ecommerce.dto.oms.model.ReturnItemDTO;
import com.itsthatjun.ecommerce.dto.oms.model.ReturnLogDTO;
import com.itsthatjun.ecommerce.dto.oms.model.ReturnPictureDTO;
import com.itsthatjun.ecommerce.dto.oms.model.ReturnRequestDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ReturnDetail implements Serializable {

    private ReturnRequestDTO returnRequest;

    /**
     * Return item list
     */
    private List<ReturnItemDTO> returnItemList;

    /**
     * Return picture list
     */
    private List<ReturnPictureDTO> picturesList;

    /**
     * Return log list
     */
    private List<ReturnLogDTO> logList;
}
