package com.itsthatjun.ecommerce.repository.as;

import com.itsthatjun.ecommerce.model.entity.ProductAlbum;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductAlbumRepository extends ReactiveCrudRepository<ProductAlbum, Integer> {
}
