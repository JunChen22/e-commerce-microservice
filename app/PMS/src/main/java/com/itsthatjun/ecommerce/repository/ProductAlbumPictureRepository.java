package com.itsthatjun.ecommerce.repository.as;

import com.itsthatjun.ecommerce.model.entity.ProductAlbumPicture;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductAlbumPictureRepository extends ReactiveCrudRepository<ProductAlbumPicture, Integer> {
}
