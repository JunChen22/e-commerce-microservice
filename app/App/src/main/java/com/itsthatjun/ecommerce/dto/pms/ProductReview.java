package com.itsthatjun.ecommerce.dto.pms;

import com.itsthatjun.ecommerce.dto.pms.model.ReviewDTO;
import com.itsthatjun.ecommerce.dto.pms.model.ReviewPictureDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductReview {

    private List<ReviewDTO> reviews;

    private int albumId;

    private List<ReviewPictureDTO> picturesList;
}
