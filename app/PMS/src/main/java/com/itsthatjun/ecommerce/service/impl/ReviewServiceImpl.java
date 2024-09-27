package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.dto.ProductReview;
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
        return Mono.fromCallable(() -> {
            // Offload the blocking operation to the specified scheduler
            Review review = reviewMapper.selectByPrimaryKey(reviewId);
            ProductReview productReview = new ProductReview();
            //productReview.setReview(review); TODO:

            ReviewAlbumExample albumExample = new ReviewAlbumExample();
            albumExample.createCriteria().andReviewIdEqualTo(reviewId);
            ReviewAlbum foundAlbum = albumMapper.selectByExample(albumExample).get(0);

            int albumId = foundAlbum.getId();

            ReviewPicturesExample reviewPicturesExample = new ReviewPicturesExample();
            reviewPicturesExample.createCriteria().andReviewAlbumIdEqualTo(albumId);
            List<ReviewPictures> reviewPicturesList = picturesMapper.selectByExample(reviewPicturesExample);

            productReview.setPicturesList(reviewPicturesList);
            return productReview;
        }).subscribeOn(jdbcScheduler);
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
            //productReview.setReview(review); TODO:

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
        return Mono.fromCallable(() -> {
            // Offload the blocking operation to the specified scheduler
            newReview.setMemberId(userId);
            reviewMapper.insert(newReview);
            int reviewId = newReview.getId();
            createAlbumAndPicture(reviewId, picturesList);
            createUpdateLog(reviewId, "create review", userId);
            return newReview;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Review> updateReview(Review updatedReview, List<ReviewPictures> picturesList, int userId) {
        return Mono.fromCallable(() -> {
            // Offload the blocking operation to the specified scheduler
            updatedReview.setUpdatedAt(new Date());
            reviewMapper.updateByPrimaryKeySelective(updatedReview);

            int reviewId = updatedReview.getId();

            // TODO: change the update method, currently is delete then reinsert
            deleteAlbumAndPicture(reviewId);
            createAlbumAndPicture(reviewId, picturesList);

            createUpdateLog(reviewId, "update review", userId);

            return updatedReview;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Void> deleteReview(int reviewId, int userId) {
        return Mono.fromRunnable(() -> {
            reviewMapper.deleteByPrimaryKey(reviewId);
            deleteAlbumAndPicture(reviewId);
            createUpdateLog(reviewId, "delete review", userId);
        }).subscribeOn(jdbcScheduler).then();
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
        albumMapper.insert(newAlbum);

        int albumId = newAlbum.getId();

        if (picturesList.isEmpty()) return 0;

        for (ReviewPictures picture: picturesList) {
            picture.setReviewAlbumId(albumId);

            picturesMapper.insert(picture);
        }
        return picturesList.size();
    }

    private void createUpdateLog(int reviewId, String updateAction, int userId) {
        // TODO: might create another update log table just for user and one for admin
        ReviewUpdateLog updateLog = new ReviewUpdateLog();
        updateLog.setReviewId(reviewId);
        updateLog.setUpdateAction(updateAction);
        updateLog.setOperator("user id: " + userId);
        updateLogMapper.insert(updateLog);
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
            //productReview.setReview(review); TODO:

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
    public Mono<Review> adminCreateReview(Review newReview, List<ReviewPictures> picturesList, String operator) {
        return Mono.fromCallable(() -> {
            // Offload the blocking operation to the specified scheduler
            reviewMapper.insert(newReview);

            int reviewId = newReview.getId();

            createAlbumAndPicture(reviewId, picturesList);
            createAdminUpdateLog(reviewId, "create review", operator);
            return newReview;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Review> adminUpdateReview(Review updatedReview, List<ReviewPictures> picturesList, String operator) {
        return Mono.fromCallable(() -> {
            // Offload the blocking operation to the specified scheduler
            Date currentTime = new Date();
            updatedReview.setUpdatedAt(currentTime);

            int reviewId = updatedReview.getId();
            deleteAlbumAndPicture(reviewId);
            createAlbumAndPicture(reviewId, picturesList);

            reviewMapper.updateByPrimaryKeySelective(updatedReview);
            createAdminUpdateLog(reviewId, "update review", operator);
            return updatedReview;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Void> adminDeleteReview(int reviewId, String operator) {
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

            createAdminUpdateLog(reviewId, "delete review", operator);
        }).subscribeOn(jdbcScheduler).then();
    }

    private void createAdminUpdateLog(int reviewId, String updateAction, String operator) {
        ReviewUpdateLog updateLog = new ReviewUpdateLog();
        updateLog.setReviewId(reviewId);
        updateLog.setUpdateAction(updateAction);
        updateLog.setOperator(operator);
        updateLogMapper.insert(updateLog);
    }
}
