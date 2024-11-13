package com.itsthatjun.ecommerce.dto.oms;

import com.itsthatjun.ecommerce.dto.oms.model.ReturnPictureDTO;
import com.itsthatjun.ecommerce.dto.oms.model.ReturnRequestDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ReturnParam extends ReturnRequestDTO implements Serializable {

    /**
     * Return item list
     */
    private Map<String, Integer> skuQuantity;

    /**
     * Return pictures list
     */
    private List<ReturnPictureDTO> picturesList;
}
