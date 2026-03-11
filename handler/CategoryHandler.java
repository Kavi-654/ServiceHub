
package com.serviceplatform.handlers;

import com.serviceplatform.dao.CategoryDAO;
import com.serviceplatform.models.Category;
import java.util.List;

/**
 * CategoryHandler - Handles category-related business logic
 * 
 * Responsibilities:
 * - Fetch all categories
 * - Fetch category by ID
 * - Validate category exists
 */
public class CategoryHandler {
    
    private CategoryDAO categoryDAO;
    
    /**
     * Constructor - Initialize DAO
     */
    public CategoryHandler() {
        this.categoryDAO = new CategoryDAO();
    }
    
    /**
     * Get all categories
     * 
     * Business Logic:
     * - Fetch all active categories from database
     * - Return list (can be empty if no categories exist)
     * 
     * @return List<Category> - All categories
     */
    public List<Category> getAllCategories() {
        try {
            // Simple DAO call - no business logic needed here
            return categoryDAO.findAll();
            
        } catch (Exception e) {
            System.err.println("Error in CategoryHandler.getAllCategories(): " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get category by ID
     * 
     * Business Logic:
     * - Validate ID is positive
     * - Fetch category from database
     * - Return null if not found
     * 
     * @param categoryId - Category ID to fetch
     * @return Category - Found category or null
     */
    public Category getCategoryById(int categoryId) {
        try {
            // BUSINESS RULE: Validate ID
            if (categoryId <= 0) {
                System.err.println("Invalid category ID: " + categoryId);
                return null;
            }
            
            // Fetch from DAO
            return categoryDAO.findCategoryById(categoryId);
            
        } catch (Exception e) {
            System.err.println("Error in CategoryHandler.getCategoryById(): " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Check if category exists
     * 
     * Business Logic:
     * - Used by other handlers to validate category
     * - Returns true if category exists and is valid
     * 
     * @param categoryId - Category ID to check
     * @return boolean - true if exists, false otherwise
     */
    public boolean categoryExists(int categoryId) {
        try {
            if (categoryId <= 0) {
                return false;
            }
            
            Category category = categoryDAO.findCategoryById(categoryId);
            return category != null;
            
        } catch (Exception e) {
            System.err.println("Error in CategoryHandler.categoryExists(): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
}
