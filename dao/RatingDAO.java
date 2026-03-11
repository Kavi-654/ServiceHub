package com.serviceplatform.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.serviceplatform.models.Rating;
import com.serviceplatform.queryexecutor.QueryExecutor;
import com.serviceplatform.querygenerator.QueryGenerator;
import com.serviceplatform.utils.DbConnection;
import com.serviceplatform.dao.BusinessDAO;

public class RatingDAO {
    
    private BusinessDAO businessDAO = new BusinessDAO();
    
    /**
     * Insert new rating AND update business rating
     * This is a TRANSACTION - both operations must succeed or both fail
     * 
     * Returns: Generated rating_id
     * 
     * Example Usage:
     * Rating rating = new Rating();
     * rating.setTransactionId(123);
     * rating.setUserId(42);
     * rating.setProviderId(56);
     * rating.setBusinessId(78);
     * rating.setNoOfStars(5);
     * rating.setReview("Excellent service!");
     * 
     * int ratingId = ratingDAO.insert(rating);
     */
    public int insert(Rating rating) throws SQLException {
        // STEP 1: Check if rating already exists for this transaction
        if (hasRatingForTransaction(rating.getTransactionId())) {
            throw new SQLException("Rating already exists for this transaction");
        }
        
        // STEP 2: Insert rating
        String query = QueryGenerator.generateInsert(
            "Rating",
            "transaction_id, user_id, provider_id, business_id, no_of_stars, review",
            6
        );
        
        int generatedRatingId = QueryExecutor.executeInsert(
            query,
            rating.getTransactionId(),
            rating.getUserId(),
            rating.getProviderId(),
            rating.getBusinessId(),
            rating.getNoOfStars(),
            rating.getReview()
        );
        
        // STEP 3: Calculate new average rating for the business
        RatingStats stats = getAverageRating(rating.getBusinessId());
        
        // STEP 4: Update business with new rating
        businessDAO.updateRating(
            rating.getBusinessId(),
            stats.avgRating,
            stats.totalRatings
        );
        
        return generatedRatingId;
    }
    
    /**
     * Find rating by rating_id
     * Returns: Rating object or null if not found
     */
    public Rating findById(int ratingId) throws SQLException {
        String query = QueryGenerator.generateSelectByOne("Rating", "rating_id");
        ResultSet rs = QueryExecutor.executeSelectOne(query, ratingId);
        
        try {
            if (rs.next()) {
                return mapResultSetToRating(rs);
            }
            return null;
        } finally {
            DbConnection.closeResources(null, null, rs);
        }
    }
    
    /**
     * Find rating by transaction_id
     * Returns: Rating object or null if not found
     */
    public Rating findByTransactionId(int transactionId) throws SQLException {
        String query = QueryGenerator.generateSelectByOne("Rating", "transaction_id");
        ResultSet rs = QueryExecutor.executeSelectOne(query, transactionId);
        
        try {
            if (rs.next()) {
                return mapResultSetToRating(rs);
            }
            return null;
        } finally {
            DbConnection.closeResources(null, null, rs);
        }
    }
    
    /**
     * Find all ratings for a business
     * Returns: List of ratings (ordered by newest first)
     * Used to display reviews on business profile
     */
    public List<Rating> findByBusinessId(int businessId) throws SQLException {
        String query = QueryGenerator.generateRatingsWithUserInfo();
        ResultSet rs = QueryExecutor.executeSelectMultiple(query, businessId);
        
        List<Rating> ratings = new ArrayList<>();
        
        try {
            while (rs.next()) {
                Rating rating = mapResultSetToRating(rs);
                
                // If your Rating model has additional user info fields:
                // rating.setUserName(rs.getString("user_name"));
                // rating.setUserEmail(rs.getString("email"));
                
                ratings.add(rating);
            }
            return ratings;
        } finally {
            DbConnection.closeResources(null, null, rs);
        }
    }
    
    /**
     * Get average rating and total count for a business
     * Returns: RatingStats object with avgRating and totalRatings
     */
    public RatingStats getAverageRating(int businessId) throws SQLException {
        String query = QueryGenerator.generateAverageRating("Rating", "no_of_stars", "business_id");
        ResultSet rs = QueryExecutor.executeSelectOne(query, businessId);
        
        try {
            if (rs.next()) {
                double avgRating = rs.getDouble("avg_rating");
                int totalRatings = rs.getInt("total_ratings");
                
                return new RatingStats(avgRating, totalRatings);
            }
            return new RatingStats(0.0, 0);  // No ratings yet
        } finally {
            DbConnection.closeResources(null, null, rs);
        }
    }
    
    /**
     * Check if rating already exists for a transaction
     * Prevents duplicate ratings
     * Returns: true if rating exists, false otherwise
     */
    public boolean hasRatingForTransaction(int transactionId) throws SQLException {
        String query = QueryGenerator.generateCheckRatingExists();
        int count = QueryExecutor.executeCount(query, transactionId);
        
        return count > 0;
    }
    
    /**
     * Find all ratings by user (ratings given by this user)
     * Returns: List of ratings
     */
    public List<Rating> findByUserId(int userId) throws SQLException {
        String query = QueryGenerator.generateSelectWithOrder(
            "Rating",
            "user_id",
            "created_at DESC"
        );
        
        ResultSet rs = QueryExecutor.executeSelectMultiple(query, userId);
        List<Rating> ratings = new ArrayList<>();
        
        try {
            while (rs.next()) {
                ratings.add(mapResultSetToRating(rs));
            }
            return ratings;
        } finally {
            DbConnection.closeResources(null, null, rs);
        }
    }
    
    /**
     * Find all ratings for a provider (ratings received by this provider)
     * Returns: List of ratings
     */
    public List<Rating> findByProviderId(int providerId) throws SQLException {
        String query = QueryGenerator.generateSelectWithOrder(
            "Rating",
            "provider_id",
            "created_at DESC"
        );
        
        ResultSet rs = QueryExecutor.executeSelectMultiple(query, providerId);
        List<Rating> ratings = new ArrayList<>();
        
        try {
            while (rs.next()) {
                ratings.add(mapResultSetToRating(rs));
            }
            return ratings;
        } finally {
            DbConnection.closeResources(null, null, rs);
        }
    }
    
    /**
     * Helper: Convert ResultSet to Rating object
     * Expects rs.next() to have been called already
     */
    private Rating mapResultSetToRating(ResultSet rs) throws SQLException {
        Rating rating = new Rating();
        
        // Map all columns from Rating table
        rating.setRatingId(rs.getInt("rating_id"));
        rating.setTransactionId(rs.getInt("transaction_id"));
        rating.setUserId(rs.getInt("user_id"));
        rating.setProviderId(rs.getInt("provider_id"));
        rating.setBusinessId(rs.getInt("business_id"));
        rating.setNoOfStars(rs.getInt("no_of_stars"));
        rating.setReview(rs.getString("review"));
        rating.setCreatedAt(rs.getTimestamp("created_at"));
        rating.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        return rating;
    }
    
    /**
     * Inner class to hold rating statistics
     */
    public static class RatingStats {
        public double avgRating;
        public int totalRatings;
        
        public RatingStats(double avgRating, int totalRatings) {
            this.avgRating = avgRating;
            this.totalRatings = totalRatings;
        }
    }
}