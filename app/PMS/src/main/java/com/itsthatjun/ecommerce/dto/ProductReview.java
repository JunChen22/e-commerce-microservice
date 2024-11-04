package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.dto.model.ReviewDTO;
import com.itsthatjun.ecommerce.model.entity.ReviewAlbumPicture;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ProductReview extends ReviewDTO implements Serializable {

    private List<ReviewAlbumPicture> picturesList;
    //private List<ReviewDTO> reviews;
}
