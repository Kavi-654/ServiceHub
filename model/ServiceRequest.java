package com.serviceplatform.models;

import com.serviceplatform.enums.ServiceRequestStatus;
import com.serviceplatform.enums.PriorityLevel;
import java.sql.Timestamp;

public class ServiceRequest {
    private int serviceRequestId;
    private int userId;
    private String title;
    private String description;
    private int categoryId;
    private String location;
    private PriorityLevel priority;
    private ServiceRequestStatus status;
    private boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Default Constructor
    public ServiceRequest() {}
    
    // Parameterized Constructor
    public ServiceRequest(int serviceRequestId, int userId, String title, 
                          String description, int categoryId, String location, 
                          PriorityLevel priority, ServiceRequestStatus status, 
                          boolean isActive, Timestamp createdAt, Timestamp updatedAt) {
        this.serviceRequestId = serviceRequestId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.location = location;
        this.priority = priority;
        this.status = status;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public int getServiceRequestId() {
        return serviceRequestId;
    }
    
    public void setServiceRequestId(int serviceRequestId) {
        this.serviceRequestId = serviceRequestId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
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
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public PriorityLevel getPriority() {
        return priority;
    }
    
    public void setPriorityLevel(PriorityLevel priority) {
        this.priority = priority;
    }
    
    public ServiceRequestStatus getStatus() {
        return status;
    }
    
    public void setStatus(ServiceRequestStatus status) {
        this.status = status;
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