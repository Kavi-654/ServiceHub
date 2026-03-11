package com.serviceplatform.models;

import java.sql.Timestamp;

public class Rating {
    private int ratingId;
    private int transactionId;
    private int userId;
    private int providerId;
    private int businessId;
    private int noOfStars;
    private String review;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Default Constructor
    public Rating() {}
    
    // Parameterized Constructor
    public Rating(int ratingId, int transactionId, int userId, int providerId, 
                  int businessId, int noOfStars, String review, 
                  Timestamp createdAt, Timestamp updatedAt) {
        this.ratingId = ratingId;
        this.transactionId = transactionId;
        this.userId = userId;
        this.providerId = providerId;
        this.businessId = businessId;
        this.noOfStars = noOfStars;
        this.review = review;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public int getRatingId() {
        return ratingId;
    }
    
    public void setRatingId(int ratingId) {
        this.ratingId = ratingId;
    }
    
    public int getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public int getProviderId() {
        return providerId;
    }
    
    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }
    
    public int getBusinessId() {
        return businessId;
    }
    
    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }
    
    public int getNoOfStars() {
        return noOfStars;
    }
    
    public void setNoOfStars(int noOfStars) {
        this.noOfStars = noOfStars;
    }
    
    public String getReview() {
        return review;
    }
    
    public void setReview(String review) {
        this.review = review;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}