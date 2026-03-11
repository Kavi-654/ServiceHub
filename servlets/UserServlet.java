package com.serviceplatform.servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.serviceplatform.handlers.BusinessHandler;
import com.serviceplatform.handlers.CategoryHandler;
import com.serviceplatform.handlers.UserHandler;
import com.serviceplatform.models.Business;
import com.serviceplatform.models.Category;
import com.serviceplatform.models.User;
import com.serviceplatform.utils.ResponseUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/user")
public class UserServlet extends HttpServlet {

    private UserHandler userHandler;
    private BusinessHandler businessHandler;
    private CategoryHandler categoryHandler;
    
    @Override
    public void init() throws ServletException {
        userHandler = new UserHandler();
        businessHandler = new BusinessHandler();
        categoryHandler = new CategoryHandler();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if (action == null) {
            ResponseUtil.sendError(response, "Action parameter is required");
            return;
        }
        
        switch (action) {
            case "register":
                handleRegister(request, response);
                break;
            case "login":
                handleLogin(request, response);
                break;
            case "logout":
                handleLogout(request, response);
                break;
            case "updateProfile":
                handleUpdateProfile(request, response);
                break;
            case "becomeProvider":
                handleBecomeProvider(request, response);
                break;
            case "registerBusiness":
                handleRegisterBusiness(request, response);
                break;
            case "updateBusiness":
                handleUpdateBusiness(request, response);
                break;
            default:
                ResponseUtil.sendError(response, "Invalid action: " + action);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if (action == null) {
            ResponseUtil.sendError(response, "Action parameter is required");
            return;
        }
        
        switch (action) {
            case "profile":
                handleGetProfile(request, response);
                break;
            case "myBusiness":
                handleGetMyBusiness(request, response);
                break;
            case "getCategories":
                handleGetCategories(request, response);
                break;
            case "searchProviders":
                handleSearchProviders(request, response);
                break;
            default:
                ResponseUtil.sendError(response, "Invalid action: " + action);
        }
    }
    
    // ==================== POST HANDLERS ====================
    
    /**
     * Register new user
     */
    private void handleRegister(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            // Get parameters
            String userName = request.getParameter("userName");
            String email = request.getParameter("email");
            String phoneNumber = request.getParameter("phoneNumber");
            String password = request.getParameter("password");
            String bio = request.getParameter("bio");
            
            // Validate required fields
            if (userName == null || email == null || password == null) {
                ResponseUtil.sendError(response, "Missing required fields: userName, email, password");
                return;
            }
            
            // Register user
            int userId = userHandler.registerUser(userName, email, phoneNumber, password, bio);
            
            if (userId > 0) {
                ResponseUtil.sendSuccess(response, "Registration successful", new IdResponse(userId));
            } else {
                ResponseUtil.sendError(response, "Registration failed");
            }
            
        } catch (Exception e) {
            ResponseUtil.sendError(response, "Error: " + e.getMessage());
        }
    }
    
    /**
     * Login user
     */
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            // Get parameters
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            
            // Validate
            if (email == null || password == null) {
                ResponseUtil.sendError(response, "Email and password are required");
                return;
            }
            
            // Authenticate
            User user = userHandler.loginUser(email, password);
            
            if (user != null) {
                // CREATE SESSION
                HttpSession session = request.getSession(true);
                session.setAttribute("userId", user.getUserId());
                session.setAttribute("userName", user.getUserName());
                session.setAttribute("email", user.getEmail());
                session.setAttribute("isProvider", user.isProviderStatus());
                session.setMaxInactiveInterval(30 * 60);  // 30 minutes
                
                // Send response
                ResponseUtil.sendSuccess(response, "Login successful");
            } else {
                ResponseUtil.sendError(response, "Invalid email or password");
            }
            
        } catch (Exception e) {
            ResponseUtil.sendError(response, "Error: " + e.getMessage());
        }
    }
    
    /**
     * Logout user
     */
    private void handleLogout(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            HttpSession session = request.getSession(false);
            
            if (session != null) {
                session.invalidate();
            }
            
            ResponseUtil.sendSuccess(response, "Logout successful");
            
        } catch (Exception e) {
            ResponseUtil.sendError(response, "Error: " + e.getMessage());
        }
    }
    
    /**
     * Update user profile
     */
    private void handleUpdateProfile(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // Check authentication
        if (!isLoggedIn(request)) {
            ResponseUtil.sendError(response, "Not authenticated. Please login");
            return;
        }
        
        Integer userId = getLoggedInUserId(request);
        
        try {
            // Get update parameters
            String userName = request.getParameter("userName");
            String email = request.getParameter("email");
            String phoneNumber = request.getParameter("phoneNumber");
            String bio = request.getParameter("bio");
            
            // Update profile
            boolean updated = userHandler.updateProfile(userId, userName, email, phoneNumber, bio);
            
            if (updated) {
                // Update session with new name
                HttpSession session = request.getSession(false);
                if (session != null && userName != null) {
                    session.setAttribute("userName", userName);
                }
                
                ResponseUtil.sendSuccess(response, "Profile updated successfully");
            } else {
                ResponseUtil.sendError(response, "Profile update failed");
            }
            
        } catch (Exception e) {
            ResponseUtil.sendError(response, "Error: " + e.getMessage());
        }
    }
    
    /**
     * Make user a provider
     */
    private void handleBecomeProvider(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // Check authentication
        if (!isLoggedIn(request)) {
            ResponseUtil.sendError(response, "Not authenticated. Please login");
            return;
        }
        
        Integer userId = getLoggedInUserId(request);
        
        try {
            boolean updateProvider = userHandler.becomeProvider(userId);
            
            if (updateProvider) {
                // Update session
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.setAttribute("isProvider", true);
                }
                
                ResponseUtil.sendSuccess(response, "You are now a service provider!");
            } else {
                ResponseUtil.sendError(response, "Failed to become provider");
            }
            
        } catch (Exception e) {
            ResponseUtil.sendError(response, "Error: " + e.getMessage());
        }
    }
    
    /**
     * Register business
     */
    private void handleRegisterBusiness(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // Check authentication
        if (!isLoggedIn(request)) {
            ResponseUtil.sendError(response, "Not authenticated. Please login");
            return;
        }
        
        Integer userId = getLoggedInUserId(request);
        
        try {
            // Get parameters
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            String categoryIdParam = request.getParameter("categoryId");
            
            // Validate
            if (name == null || categoryIdParam == null) {
                ResponseUtil.sendError(response, "Business name and category are required");
                return;
            }
            
            int categoryId = Integer.parseInt(categoryIdParam);
            
            // Register business
            int businessId = businessHandler.registerBusiness(userId, name, description, categoryId);
            
            if (businessId > 0) {
                ResponseUtil.sendSuccess(response, "Business registered successfully", 
                    new IdResponse(businessId));
            } else {
                ResponseUtil.sendError(response, "Business registration failed");
            }
            
        } catch (NumberFormatException e) {
            ResponseUtil.sendError(response, "Invalid category ID format");
        } catch (Exception e) {
            ResponseUtil.sendError(response, "Error: " + e.getMessage());
        }
    }
    
    /**
     * Update business
     */
    private void handleUpdateBusiness(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // Check authentication
        if (!isLoggedIn(request)) {
            ResponseUtil.sendError(response, "Not authenticated. Please login");
            return;
        }
        
        try {
            // Get parameters
            String businessIdParam = request.getParameter("businessId");
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            String categoryIdParam = request.getParameter("categoryId");
            
            // Validate
            if (businessIdParam == null || name == null || categoryIdParam == null) {
                ResponseUtil.sendError(response, "Business ID, name, and category are required");
                return;
            }
            
            int businessId = Integer.parseInt(businessIdParam);
            int categoryId = Integer.parseInt(categoryIdParam);
            
            // Update business
            boolean updated = businessHandler.updateBusiness(businessId, name, description, categoryId);
            
            if (updated) {
                ResponseUtil.sendSuccess(response, "Business updated successfully");
            } else {
                ResponseUtil.sendError(response, "Business update failed");
            }
            
        } catch (NumberFormatException e) {
            ResponseUtil.sendError(response, "Invalid business ID or category ID format");
        } catch (Exception e) {
            ResponseUtil.sendError(response, "Error: " + e.getMessage());
        }
    }
    
    // ==================== GET HANDLERS ====================
    
    /**
     * Get logged-in user's profile
     */
    private void handleGetProfile(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // Check authentication
        if (!isLoggedIn(request)) {
            ResponseUtil.sendError(response, "Not authenticated. Please login");
            return;
        }
        
        Integer userId = getLoggedInUserId(request);
        
        try {
            User user = userHandler.getUserById(userId);
            
            if (user != null) {
                ResponseUtil.sendSuccess(response, "Profile retrieved", user);
            } else {
                ResponseUtil.sendError(response, "User not found");
            }
            
        } catch (SQLException e) {
            ResponseUtil.sendError(response, "Error: " + e.getMessage());
        }
    }
    
    /**
     * Get logged-in user's businesses
     */
    private void handleGetMyBusiness(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // Check authentication
        if (!isLoggedIn(request)) {
            ResponseUtil.sendError(response, "Not authenticated. Please login");
            return;
        }
        
        Integer userId = getLoggedInUserId(request);
        
        try {
            List<Business> businesses = businessHandler.getMyBusinesses(userId);
            
            if (businesses != null && !businesses.isEmpty()) {
                ResponseUtil.sendSuccess(response, "Businesses retrieved", businesses);
            } else {
                ResponseUtil.sendError(response, "No businesses found");
            }
            
        } catch (Exception e) {
            ResponseUtil.sendError(response, "Error: " + e.getMessage());
        }
    }
    
    /**
     * Get all categories
     */
    private void handleGetCategories(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            List<Category> categories = categoryHandler.getAllCategories();
            
            if (categories != null && !categories.isEmpty()) {
                ResponseUtil.sendSuccess(response, "Categories retrieved", categories);
            } else {
                ResponseUtil.sendError(response, "No categories found");
            }
            
        } catch (Exception e) {
            ResponseUtil.sendError(response, "Error: " + e.getMessage());
        }
    }
    
    /**
     * Search providers by category
     */
    private void handleSearchProviders(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            String categoryIdParam = request.getParameter("categoryId");
            
            if (categoryIdParam == null) {
                ResponseUtil.sendError(response, "Category ID is required");
                return;
            }
            
            int categoryId = Integer.parseInt(categoryIdParam);
            List<Business> providers = businessHandler.getProvidersByCategory(categoryId);
            
            if (providers != null && !providers.isEmpty()) {
                ResponseUtil.sendSuccess(response, "Providers found", providers);
            } else {
                ResponseUtil.sendSuccess(response, "No providers found for this category", null);
            }
            
        } catch (NumberFormatException e) {
            ResponseUtil.sendError(response, "Invalid category ID format");
        } catch (Exception e) {
            ResponseUtil.sendError(response, "Error: " + e.getMessage());
        }
    }
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Check if user is logged in
     */
    private boolean isLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("userId") != null;
    }
    
    /**
     * Get logged-in user ID
     */
    private Integer getLoggedInUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (Integer) session.getAttribute("userId");
        }
        return null;
    }
    
    // ==================== RESPONSE CLASS ====================
    
    /**
     * Simple ID response wrapper
     */
    class IdResponse {
        int id;
        
        IdResponse(int id) {
            this.id = id;
        }
    }
}