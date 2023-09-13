package com.itsthatjun.ecommerce.dto.pms;

import com.itsthatjun.ecommerce.mbg.model.Review;
import com.itsthatjun.ecommerce.mbg.model.ReviewPictures;
import lombok.Data;

import java.util.List;

@Data
public class ProductReview {
    private Review review;
    private List<ReviewPictures> picturesList;
}
