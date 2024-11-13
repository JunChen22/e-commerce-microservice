package com.itsthatjun.ecommerce.repository.as;

import com.itsthatjun.ecommerce.model.entity.ReviewAlbumPicture;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewAlbumPictureRepository extends ReactiveCrudRepository<ReviewAlbumPicture, Integer> {
}
