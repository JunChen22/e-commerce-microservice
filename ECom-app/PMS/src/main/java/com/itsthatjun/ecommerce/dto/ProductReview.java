package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.dto.model.ReviewDTO;
import com.itsthatjun.ecommerce.mbg.model.ReviewPictures;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductReview extends ReviewDTO {

    private List<ReviewPictures> picturesList;

}
