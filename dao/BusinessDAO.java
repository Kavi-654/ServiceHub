package com.serviceplatform.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.serviceplatform.models.Business;
import com.serviceplatform.queryexecutor.QueryExecutor;
import com.serviceplatform.querygenerator.QueryGenerator;
import com.serviceplatform.utils.DbConnection;

public class BusinessDAO {

    /**
     * Insert new business profile
     * Returns: Generated business_id
     */
    public int insert(Business business) throws SQLException {
        String query = QueryGenerator.generateInsert(
            "Business",
            "owner_id, name, description, category_id, avg_rating, total_ratings, is_active",
            7
        );

        int generatedId = QueryExecutor.executeInsert(
            query,
            business.getOwnerId(),
            business.getName(),
            business.getDescription(),
            business.getCategoryId(),
            business.getAvgRating(),
            business.getTotalRatings(),
            business.isActive()
        );
        
        return generatedId;
    }

    /**
     * Find business by ID
     * Returns: Business object or null if not found
     */
    public Business findById(int businessId) throws SQLException {
        String query = QueryGenerator.generateSelectByOne("Business", "business_id");
        ResultSet rs = QueryExecutor.executeSelectOne(query, businessId);

        try {
            if (rs.next()) {
                return mapResultSetToBusiness(rs);
            }
            return null;
        } finally {
            DbConnection.closeResources(null, null, rs);
        }
    }

    /**
     * Find ALL businesses owned by a user
     * Returns: List of businesses (can be multiple!)
     * 
     * Example:
     * - John has: John's Plumbing, John's Electrical
     * - Returns both businesses
     */
    public List<Business> findByOwnerId(int ownerId) throws SQLException {
        String query = QueryGenerator.generateSelectByOne("Business", "owner_id");
        ResultSet rs = QueryExecutor.executeSelectMultiple(query, ownerId);  // ✅ Multiple results

        List<Business> businesses = new ArrayList<>();
        
        try {
            while (rs.next()) {
                Business business = mapResultSetToBusiness(rs);
                businesses.add(business);
            }
            return businesses;  // ✅ Returns list (can be empty, 1, or many)
        } finally {
            DbConnection.closeResources(null, null, rs);
        }
    }

    /**
     * Find all businesses by category
     * Returns: List of businesses in that category
     * Used when user searches for providers by category
     */
    public List<Business> findByCategory(int categoryId) throws SQLException {
        String query = QueryGenerator.generateBusinessesByCategory();
        ResultSet rs = QueryExecutor.executeSelectMultiple(query, categoryId);

        List<Business> businesses = new ArrayList<>();
        
        try {
            while (rs.next()) {
                Business business = mapResultSetToBusiness(rs);
                businesses.add(business);
            }
            return businesses;
        } finally {
            DbConnection.closeResources(null, null, rs);
        }
    }

    /**
     * Update business rating
     * Called after a new rating is submitted
     * Returns: true if successful
     */
    public boolean updateRating(int businessId, double avgRating, int totalRatings) throws SQLException {
        String query = QueryGenerator.generateUpdateMultiple(
            "Business",
            "avg_rating, total_ratings",
            "business_id"
        );

        return QueryExecutor.executeUpdate(
            query,
            avgRating,
            totalRatings,
            businessId
        );
    }

    /**
     * Update business profile
     * Returns: true if successful
     */
    public boolean updateProfile(Business business) throws SQLException {
        String query = QueryGenerator.generateUpdateMultiple(
            "Business",
            "name, description, category_id",
            "business_id"
        );

        return QueryExecutor.executeUpdate(
            query,
            business.getName(),
            business.getDescription(),
            business.getCategoryId(),
            business.getBusinessId()
        );
    }

    /**
     * Helper: Convert ResultSet to Business object
     * Expects rs.next() to have been called already
     */
    private static Business mapResultSetToBusiness(ResultSet rs) throws SQLException {
        Business business = new Business();

        business.setBusinessId(rs.getInt("business_id"));
        business.setOwnerId(rs.getInt("owner_id"));
        business.setName(rs.getString("name"));
        business.setDescription(rs.getString("description"));
        business.setCategoryId(rs.getInt("category_id"));
        business.setAvgRating(rs.getDouble("avg_rating"));
        business.setTotalRatings(rs.getInt("total_ratings"));
        business.setActive(rs.getBoolean("is_active"));
        business.setCreatedAt(rs.getTimestamp("created_at"));
        business.setUpdatedAt(rs.getTimestamp("updated_at"));

        return business;
    }
}