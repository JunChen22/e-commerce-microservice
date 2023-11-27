package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.dto.outgoing.ReviewDTO;
import com.itsthatjun.ecommerce.mbg.model.ReviewPictures;
import lombok.Data;

import java.util.List;

@Data
public class ProductReview extends ReviewDTO {

    private List<ReviewPictures> picturesList;

}
