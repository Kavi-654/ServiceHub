package com.serviceplatform.handlers;

import java.sql.SQLException;
import java.util.List;

import com.serviceplatform.dao.BusinessDAO;
import com.serviceplatform.models.Business;

public class BusinessHandler {
    private BusinessDAO businessDao;

    public BusinessHandler() {
        this.businessDao = new BusinessDAO();
    }

    /**
     * Register new business
     * 
     * @return business_id if successful, 0 if failed
     */
    public int registerBusiness(int ownerId, String name, String description, int categoryId) {
        try {
            Business business = new Business();
            business.setOwnerId(ownerId);
            business.setName(name);
            business.setDescription(description);
            business.setCategoryId(categoryId);
            business.setAvgRating(0.0);       // ✅ ADDED: Initial rating
            business.setTotalRatings(0);      // ✅ ADDED: No ratings yet
            business.setActive(true);         // ✅ ADDED: Active business
            
            int businessId = businessDao.insert(business);
            return businessId;
        } catch (SQLException e) {
            System.err.println("Error in registerBusiness(): " + e.getMessage());
            return 0;
        }
    }

    /**
     * Update business profile
     * 
     * @return true if successful, false otherwise
     */
    public boolean updateBusiness(int businessId, String name, String description, int categoryId) {
        try {
            Business business = businessDao.findById(businessId);
            
            if (business == null) {  // ✅ ADDED: Null check
                System.err.println("Business not found with ID: " + businessId);
                return false;
            }
            
            business.setName(name);
            business.setDescription(description);
            business.setCategoryId(categoryId);
            
            boolean update = businessDao.updateProfile(business);
            return update;
        } catch (SQLException e) {
            System.err.println("Error in updateBusiness(): " + e.getMessage());
            return false;
        }
    }

    /**
     * Get providers by category
     * 
     * @return List of businesses or null if error
     */
    public List<Business> getProvidersByCategory(int categoryId) {
        try {
            List<Business> businessList = businessDao.findByCategory(categoryId);
            return businessList;
        } catch (SQLException e) {
            System.err.println("Error in getProvidersByCategory(): " + e.getMessage());
            return null;
        }
    }

    /**
     * Get business by ID
     * 
     * @return Business object or null if not found
     */
    public Business getBusinessById(int businessId) {
        try {
            Business business = businessDao.findById(businessId);
            return business;
        } catch (SQLException e) {
            System.err.println("Error in getBusinessById(): " + e.getMessage());
            return null;
        }
    }

    /**
     * Get all businesses owned by user
     * 
     * @return List of businesses or null if error
     */
    public List<Business> getMyBusinesses(int ownerId) {  
        try {
            List<Business> businesses = businessDao.findByOwnerId(ownerId);
            return businesses;
        } catch (SQLException e) {
            System.err.println("Error in getMyBusinesses(): " + e.getMessage());
            return null;
        }
    }
    
   
}