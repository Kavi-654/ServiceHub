package com.serviceplatform.models;

import java.sql.Timestamp;

public class Business {
    private int businessId;
    private int ownerId;
    private String name;
    private String description;
    private int categoryId;
    private double avgRating;
    private int totalRatings;
    private boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Default Constructor
    public Business() {}
    
    // Parameterized Constructor
    public Business(int businessId, int ownerId, String name, String description, 
                    int categoryId, double avgRating, 
                    int totalRatings, boolean isActive, Timestamp createdAt, 
                    Timestamp updatedAt) {
        this.businessId = businessId;
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.avgRating = avgRating;
        this.totalRatings = totalRatings;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public int getBusinessId() {
        return businessId;
    }
    
    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }
    
    public int getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    
   
    
    public double getAvgRating() {
        return avgRating;
    }
    
    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }
    
    public int getTotalRatings() {
        return totalRatings;
    }
    
    public void setTotalRatings(int totalRatings) {
        this.totalRatings = totalRatings;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
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