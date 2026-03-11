package com.serviceplatform.handlers;

import java.sql.SQLException;
import java.util.List;

import com.serviceplatform.dao.RatingDAO;
import com.serviceplatform.dao.RatingDAO.RatingStats;
import com.serviceplatform.models.Rating;

public class RatingHandler {
    private RatingDAO ratingDAO;

    public RatingHandler() {
        this.ratingDAO = new RatingDAO();
    }

    /**
     * Submit rating
     * Automatically updates business rating!
     * 
     * @return rating_id if successful, 0 if failed
     */
    public int submitRating(int transactionId, int userId, int providerId, 
                           int businessId, int stars, String review) {
        try {
            // STEP 1: Validate stars (1-5)
            if (stars < 1 || stars > 5) {
                throw new IllegalArgumentException("Stars must be between 1 and 5");
            }

            // STEP 2: Check if already rated (prevent duplicate)
            boolean alreadyRated = ratingDAO.hasRatingForTransaction(transactionId);
            
            if (alreadyRated) {
                System.err.println("Transaction already rated!");
                return 0;
            }

            // STEP 3: Create rating object
            Rating rating = new Rating();
            rating.setTransactionId(transactionId);
            rating.setUserId(userId);
            rating.setProviderId(providerId);
            rating.setBusinessId(businessId);
            rating.setNoOfStars(stars);
            rating.setReview(review);

            // STEP 4: Insert rating (automatically updates business rating)
            int ratingId = ratingDAO.insert(rating);
            return ratingId;

        } catch (SQLException e) {
            System.err.println("Error in submitRating(): " + e.getMessage());
            return 0;
        }
    }

    /**
     * Get all ratings for a business
     * 
     * @return List of ratings
     */
    public List<Rating> getRatingsForBusiness(int businessId) {
        try {
            List<Rating> ratings = ratingDAO.findByBusinessId(businessId);
            return ratings;
        } catch (SQLException e) {
            System.err.println("Error in getRatingsForBusiness(): " + e.getMessage());
            return null;
        }
    }

    /**
     * Get average rating for a business
     * 
     * @return RatingStats object with avgRating and totalRatings
     */
    public RatingStats getAverageRating(int businessId) {
        try {
            RatingStats stats = ratingDAO.getAverageRating(businessId);
            return stats;
        } catch (SQLException e) {
            System.err.println("Error in getAverageRating(): " + e.getMessage());
            return new RatingStats(0.0, 0);  // Return default
        }
    }

    /**
     * Check if transaction has been rated
     * 
     * @return true if rated, false otherwise
     */
    public boolean hasRated(int transactionId) {
        try {
            return ratingDAO.hasRatingForTransaction(transactionId);
        } catch (SQLException e) {
            System.err.println("Error in hasRated(): " + e.getMessage());
            return false;
        }
    }

    /**
     * Get rating by transaction ID
     * 
     * @return Rating object or null
     */
    public Rating getRatingByTransactionId(int transactionId) {
        try {
            return ratingDAO.findByTransactionId(transactionId);
        } catch (SQLException e) {
            System.err.println("Error in getRatingByTransactionId(): " + e.getMessage());
            return null;
        }
    }
    
}
