package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.dto.ProductReview;
import com.itsthatjun.ecommerce.exceptions.ReviewException;
import com.itsthatjun.ecommerce.mbg.mapper.ReviewAlbumMapper;
import com.itsthatjun.ecommerce.mbg.mapper.ReviewMapper;
import com.itsthatjun.ecommerce.mbg.mapper.ReviewPicturesMapper;
import com.itsthatjun.ecommerce.mbg.model.*;
import com.itsthatjun.ecommerce.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;

    private final ReviewAlbumMapper albumMapper;

    private final ReviewPicturesMapper picturesMapper;

    @Autowired
    public ReviewServiceImpl(ReviewMapper reviewMapper, ReviewAlbumMapper albumMapper, ReviewPicturesMapper picturesMapper) {
        this.reviewMapper = reviewMapper;
        this.albumMapper = albumMapper;
        this.picturesMapper = picturesMapper;
    }

    @Override
    public Mono<ProductReview> getDetailReview(int reviewId) {

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

        if (review != null) {
            return Mono.just(productReview);
        } else {
            return Mono.error(new ReviewException("Review not found"));
        }
    }

    @Override
    public Flux<ProductReview> listProductAllReview(int productId) {
        ReviewExample reviewExample = new ReviewExample();
        reviewExample.createCriteria().andProductIdEqualTo(productId);
        List<Review> productReviews = reviewMapper.selectByExample(reviewExample);

        List<ProductReview> productReviewList = new ArrayList<>();

        System.out.println(" review size " + productReviews.size());
        if (productReviews.isEmpty()) return Flux.fromIterable(productReviewList);

        for (Review review: productReviews) {
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
            System.out.println("picture lsit :" + reviewPicturesList.size());
            if (reviewPicturesList.isEmpty()) {
                productReview.setPicturesList(null);
            } else {
                productReview.setPicturesList(reviewPicturesList);
            }

            productReviewList.add(productReview);
        }

        return Flux.fromIterable(productReviewList);
    }

    @Override
    public int createReview(Review newReview, List<ReviewPictures> picturesList, int userId) {

        newReview.setCreatedAt(new Date());
        int status = reviewMapper.insert(newReview);

        int reviewId = newReview.getId();

        createAlbumAndPicture(reviewId, picturesList);

        return status;
    }

    @Override
    public int updateReview(Review updatedReview, List<ReviewPictures> picturesList, int userId) {
        ReviewExample example = new ReviewExample();
        example.createCriteria().andIdEqualTo(updatedReview.getId());
        Date currentTime = new Date();
        updatedReview.setUpdatedAt(currentTime);

        int reviewId = updatedReview.getId();

        deleteAlbumAndPicture(reviewId);
        createAlbumAndPicture(reviewId, picturesList);

        int status = reviewMapper.updateByExample(updatedReview, example);
        return status;
    }

    @Override
    public int deleteReview(int reviewId, int userId) {
        int status = reviewMapper.deleteByPrimaryKey(reviewId);
        deleteAlbumAndPicture(reviewId);
        return status;
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
    public List<ProductReview> listAllReviewByUser(int userId) {
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
    public void adminCreateReview(Review newReview, List<ReviewPictures> picturesList) {
        newReview.setCreatedAt(new Date());
        reviewMapper.insert(newReview);

        int reviewId = newReview.getId();

        createAlbumAndPicture(reviewId, picturesList);
    }

    @Override
    public void adminUpdateReview(Review updatedReview, List<ReviewPictures> picturesList) {
        ReviewExample example = new ReviewExample();
        example.createCriteria().andIdEqualTo(updatedReview.getId());
        Date currentTime = new Date();
        updatedReview.setUpdatedAt(currentTime);

        int reviewId = updatedReview.getId();

        deleteAlbumAndPicture(reviewId);
        createAlbumAndPicture(reviewId, picturesList);

        reviewMapper.updateByExample(updatedReview, example);
    }

    @Override
    public void adminDeleteReview(int reviewId) {

        reviewMapper.deleteByPrimaryKey(reviewId);

        ReviewAlbumExample albumExample = new ReviewAlbumExample();
        albumExample.createCriteria().andReviewIdEqualTo(reviewId);
        ReviewAlbum foundAlbum = albumMapper.selectByExample(albumExample).get(0);

        int albumId = foundAlbum.getId();

        albumMapper.deleteByPrimaryKey(albumId);

        ReviewPicturesExample picturesExample = new ReviewPicturesExample();
        picturesExample.createCriteria().andReviewAlbumIdEqualTo(albumId);
        List<ReviewPictures> picturesList = picturesMapper.selectByExample(picturesExample);

        if (picturesList.isEmpty()) return;

        for (ReviewPictures picture: picturesList) {
            picturesMapper.deleteByPrimaryKey(picture.getId());
        }
    }
}
