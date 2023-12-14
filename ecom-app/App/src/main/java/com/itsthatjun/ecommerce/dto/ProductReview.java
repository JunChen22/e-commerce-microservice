package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.mbg.model.Review;
import com.itsthatjun.ecommerce.mbg.model.ReviewPictures;
import lombok.Data;

import java.util.List;

@Data
public class ProductReview {
    private Review review;
    private int albumId;
    private List<ReviewPictures> picturesList;
}
