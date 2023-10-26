package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.dto.ProductReview;
import com.itsthatjun.ecommerce.exceptions.ReviewException;
import com.itsthatjun.ecommerce.mbg.mapper.ReviewAlbumMapper;
import com.itsthatjun.ecommerce.mbg.mapper.ReviewMapper;
import com.itsthatjun.ecommerce.mbg.mapper.ReviewPicturesMapper;
import com.itsthatjun.ecommerce.mbg.mapper.ReviewUpdateLogMapper;
import com.itsthatjun.ecommerce.mbg.model.*;
import com.itsthatjun.ecommerce.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;

    private final ReviewAlbumMapper albumMapper;

    private final ReviewPicturesMapper picturesMapper;

    private final ReviewUpdateLogMapper updateLogMapper;

    private final Scheduler jdbcScheduler;

    @Autowired
    public ReviewServiceImpl(ReviewMapper reviewMapper, ReviewAlbumMapper albumMapper, ReviewPicturesMapper picturesMapper,
                             ReviewUpdateLogMapper updateLogMapper,
                             @Qualifier("jdbcScheduler") Scheduler jdbcScheduler) {
        this.reviewMapper = reviewMapper;
        this.albumMapper = albumMapper;
        this.picturesMapper = picturesMapper;
        this.updateLogMapper = updateLogMapper;
        this.jdbcScheduler = jdbcScheduler;
    }

    @Override
    public Mono<ProductReview> getDetailReview(int reviewId) {
        return Mono.fromCallable(() ->
            // Offload the blocking operation to the specified scheduler
            internalGetDetailReview(reviewId)
        ).subscribeOn(jdbcScheduler);
    }

    private ProductReview internalGetDetailReview(int reviewId) { // TODO: add purchase verify status check
        Review review = reviewMapper.selectByPrimaryKey(reviewId);

        ProductReview productReview = new ProductReview();

        productReview.setReview(review);

        ReviewAlbumExample albumExample = new ReviewAlbumExample();
        albumExample.createCriteria().andReviewIdEqualTo(reviewId);
        ReviewAlbum foundAlbum = albumMapper.selectByExample(albumExample).get(0);

        int albumId = foundAlbum.getId();

        ReviewPicturesExample reviewPicturesExample = new ReviewPicturesExample();
        reviewPicturesExample.createCriteria().andReviewAlbumIdEqualTo(albumId);
        List<ReviewPictures> reviewPicturesList = picturesMapper.selectByExample(reviewPicturesExample);

        productReview.setAlbumId(albumId);
        productReview.setPicturesList(reviewPicturesList);

        return productReview;
    }

    @Override
    public Flux<ProductReview> listProductAllReview(int productId) {
        return Mono.fromCallable(() ->
            // Offload the blocking operation to the specified scheduler
            internalListProductAllReview(productId)
        ).flatMapMany(Flux::fromIterable).subscribeOn(jdbcScheduler);
    }

    private List<ProductReview> internalListProductAllReview(int productId) {
        ReviewExample reviewExample = new ReviewExample();
        reviewExample.createCriteria().andProductIdEqualTo(productId);
        List<Review> productReviews = reviewMapper.selectByExample(reviewExample);

        List<ProductReview> productReviewList = new ArrayList<>();

        if (productReviews.isEmpty()) return productReviewList;

        for (Review review: productReviews) {
            // copy over information
            ProductReview productReview = new ProductReview();
            productReview.setReview(review);

            int reviewId = review.getId();

            ReviewAlbumExample albumExample = new ReviewAlbumExample();
            albumExample.createCriteria().andReviewIdEqualTo(reviewId);
            ReviewAlbum foundAlbum = albumMapper.selectByExample(albumExample).get(0);

            int albumId = foundAlbum.getId();

            ReviewPicturesExample reviewPicturesExample = new ReviewPicturesExample();
            reviewPicturesExample.createCriteria().andReviewAlbumIdEqualTo(albumId);
            List<ReviewPictures> reviewPicturesList = picturesMapper.selectByExample(reviewPicturesExample);
            if (reviewPicturesList.isEmpty()) {
                productReview.setPicturesList(null);
            } else {
                productReview.setPicturesList(reviewPicturesList);
            }
            productReviewList.add(productReview);
        }
        return productReviewList;
    }

    @Override
    public Mono<Review> createReview(Review newReview, List<ReviewPictures> picturesList, int userId) {
        return Mono.fromCallable(() ->
            // Offload the blocking operation to the specified scheduler
             internalCreateReview(newReview, picturesList, userId)
        ).subscribeOn(jdbcScheduler);
    }

    public Review internalCreateReview(Review newReview, List<ReviewPictures> picturesList, int userId) {
        newReview.setCreatedAt(new Date());
        newReview.setMemberId(userId);
        int status = reviewMapper.insert(newReview);
        int reviewId = newReview.getId();
        createAlbumAndPicture(reviewId, picturesList);
        return newReview;
    }

    @Override
    public Mono<Review> updateReview(Review updatedReview, List<ReviewPictures> picturesList, int userId) {
        return Mono.fromCallable(() ->
            // Offload the blocking operation to the specified scheduler
            internalUpdateReview(updatedReview, picturesList, userId)
        ).subscribeOn(jdbcScheduler);
    }

    private Review internalUpdateReview(Review updatedReview, List<ReviewPictures> picturesList, int userId) {
        ReviewExample example = new ReviewExample();
        example.createCriteria().andIdEqualTo(updatedReview.getId());
        Date currentTime = new Date();
        updatedReview.setMemberId(userId);
        updatedReview.setUpdatedAt(currentTime);
        // TODO: check user id and review id
        int reviewId = updatedReview.getId();

        deleteAlbumAndPicture(reviewId);
        createAlbumAndPicture(reviewId, picturesList);

        reviewMapper.updateByExampleSelective(updatedReview, example);

        return updatedReview;
    }

    @Override
    public Mono<Void> deleteReview(int reviewId, int userId) {
        return Mono.fromRunnable(() ->
                internalDeleteReview(reviewId, userId)
        ).subscribeOn(jdbcScheduler).then();
    }

    private void internalDeleteReview(int reviewId, int userId) {
        reviewMapper.deleteByPrimaryKey(reviewId);
        deleteAlbumAndPicture(reviewId);
    }

    private int deleteAlbumAndPicture(int reviewId) {
        ReviewAlbumExample albumExample = new ReviewAlbumExample();
        albumExample.createCriteria().andReviewIdEqualTo(reviewId);
        ReviewAlbum foundAlbum = albumMapper.selectByExample(albumExample).get(0);

        int albumId = foundAlbum.getId();

        albumMapper.deleteByPrimaryKey(albumId);

        ReviewPicturesExample picturesExample = new ReviewPicturesExample();
        picturesExample.createCriteria().andReviewAlbumIdEqualTo(albumId);
        List<ReviewPictures> picturesList = picturesMapper.selectByExample(picturesExample);

        for (ReviewPictures picture: picturesList) {
            picturesMapper.deleteByPrimaryKey(picture.getId());
        }

        return picturesList.size();
    }

    private int createAlbumAndPicture(int reviewId, List<ReviewPictures> picturesList) {
        ReviewAlbum newAlbum = new ReviewAlbum();
        newAlbum.setReviewId(reviewId);
        newAlbum.setPicCount(picturesList.size());
        newAlbum.setCreatedAt(new Date());
        albumMapper.insert(newAlbum);

        int albumId = newAlbum.getId();

        if (picturesList.isEmpty()) return 0;

        for (ReviewPictures picture: picturesList) {
            picture.setReviewAlbumId(albumId);
            picture.setCreatedAt(new Date());

            picturesMapper.insert(picture);
        }
        return picturesList.size();
    }

    @Override
    public Flux<ProductReview> listAllReviewByUser(int userId) {
        return Mono.fromCallable(() ->
            // Offload the blocking operation to the specified scheduler
            internalListAllReviewByUser(userId)
        ).flatMapMany(Flux::fromIterable).subscribeOn(jdbcScheduler);
    }

    private List<ProductReview> internalListAllReviewByUser(int userId) {
        ReviewExample reviewExample = new ReviewExample();
        reviewExample.createCriteria().andMemberIdEqualTo(userId);
        List<Review> reviews = reviewMapper.selectByExample(reviewExample);

        List<ProductReview> productReviewList = new ArrayList<>();
        if (reviews.isEmpty()) {
            return productReviewList;
        }

        for (Review review: reviews) {
            // copy over infomation
            ProductReview productReview = new ProductReview();
            productReview.setReview(review);

            int reviewId = review.getId();

            ReviewAlbumExample albumExample = new ReviewAlbumExample();
            albumExample.createCriteria().andReviewIdEqualTo(reviewId);
            ReviewAlbum foundAlbum = albumMapper.selectByExample(albumExample).get(0);

            int albumId = foundAlbum.getId();

            ReviewPicturesExample reviewPicturesExample = new ReviewPicturesExample();
            reviewPicturesExample.createCriteria().andReviewAlbumIdEqualTo(albumId);
            List<ReviewPictures> reviewPicturesList = picturesMapper.selectByExample(reviewPicturesExample);

            productReview.setPicturesList(reviewPicturesList);

            productReviewList.add(productReview);
        }
        return productReviewList;
    }

    @Override
    public Mono<Review> adminCreateReview(Review newReview, List<ReviewPictures> picturesList) {
        return Mono.fromCallable(() -> {
            // Offload the blocking operation to the specified scheduler
            newReview.setCreatedAt(new Date());
            reviewMapper.insert(newReview);

            int reviewId = newReview.getId();

            createAlbumAndPicture(reviewId, picturesList);
            return newReview;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Review> adminUpdateReview(Review updatedReview, List<ReviewPictures> picturesList) {
        return Mono.fromCallable(() -> {
            // Offload the blocking operation to the specified scheduler
            Date currentTime = new Date();
            updatedReview.setUpdatedAt(currentTime);

            int reviewId = updatedReview.getId();
            deleteAlbumAndPicture(reviewId);
            createAlbumAndPicture(reviewId, picturesList);

            reviewMapper.updateByPrimaryKeySelective(updatedReview);
            return updatedReview;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Void> adminDeleteReview(int reviewId) {
        return Mono.fromRunnable(() -> {
            reviewMapper.deleteByPrimaryKey(reviewId);

            ReviewAlbumExample albumExample = new ReviewAlbumExample();
            albumExample.createCriteria().andReviewIdEqualTo(reviewId);
            ReviewAlbum foundAlbum = albumMapper.selectByExample(albumExample).get(0);

            int albumId = foundAlbum.getId();

            albumMapper.deleteByPrimaryKey(albumId);

            ReviewPicturesExample picturesExample = new ReviewPicturesExample();
            picturesExample.createCriteria().andReviewAlbumIdEqualTo(albumId);
            List<ReviewPictures> picturesList = picturesMapper.selectByExample(picturesExample);

            for (ReviewPictures picture: picturesList) {
                picturesMapper.deleteByPrimaryKey(picture.getId());
            }
        }).subscribeOn(jdbcScheduler).then();
    }
}
