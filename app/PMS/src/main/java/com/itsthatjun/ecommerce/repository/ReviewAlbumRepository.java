package com.itsthatjun.ecommerce.repository.as;

import com.itsthatjun.ecommerce.model.entity.ReviewAlbum;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewAlbumRepository extends ReactiveCrudRepository<ReviewAlbum, Integer> {
}
