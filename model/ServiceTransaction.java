package com.serviceplatform.models;

import com.serviceplatform.enums.TransactionStatus;
import java.sql.Timestamp;

public class ServiceTransaction {
    private int transactionId;
    private int requestId;
    private int providerId;
    private String message;
    private double quotedPrice;
    private TransactionStatus status;
    private boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Default Constructor
    public ServiceTransaction() {}
    
    // Parameterized Constructor
    public ServiceTransaction(int transactionId, int requestId, int providerId, 
                       String message, double quotedPrice, TransactionStatus status, 
                       boolean isActive, Timestamp createdAt, Timestamp updatedAt) {
        this.transactionId = transactionId;
        this.requestId = requestId;
        this.providerId = providerId;
        this.message = message;
        this.quotedPrice = quotedPrice;
        this.status = status;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public int getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }
    
    public int getRequestId() {
        return requestId;
    }
    
    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }
    
    public int getProviderId() {
        return providerId;
    }
    
    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public double getQuotedPrice() {
        return quotedPrice;
    }
    
    public void setQuotedPrice(double quotedPrice) {
        this.quotedPrice = quotedPrice;
    }
    
    public TransactionStatus getStatus() {
        return status;
    }
    
    public void setStatus(TransactionStatus status) {
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