package com.itsthatjun.ecommerce.mbg.model;

import java.util.Date;

public class ReviewPictures {
    private Integer id;

    private Integer reviewAlbumId;

    private String filename;

    private Date createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getReviewAlbumId() {
        return reviewAlbumId;
    }

    public void setReviewAlbumId(Integer reviewAlbumId) {
        this.reviewAlbumId = reviewAlbumId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}