package com.serviceplatform.servlets;

import java.io.IOException;
import java.util.List;

import com.serviceplatform.handlers.ServiceRequestHandler;
import com.serviceplatform.handlers.ServiceTransactionHandler;
import com.serviceplatform.handlers.RatingHandler;
import com.serviceplatform.models.ServiceRequest;
import com.serviceplatform.models.ServiceTransaction;
import com.serviceplatform.models.Rating;
import com.serviceplatform.utils.ResponseUtil;
import com.serviceplatform.dao.RatingDAO.RatingStats;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/service")
public class ServiceServlet extends HttpServlet {

    private ServiceRequestHandler requestHandler;
    private ServiceTransactionHandler transactionHandler;
    private RatingHandler ratingHandler;
    
    @Override
    public void init() throws ServletException {
        requestHandler = new ServiceRequestHandler();
        this.transactionHandler = new ServiceTransactionHandler();
        ratingHandler = new RatingHandler();
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
            case "createRequest":
                handleCreateRequest(request, response);
                break;
            case "closeRequest":
                handleCloseRequest(request, response);
                break;
            case "cancelRequest":
                handleCancelRequest(request, response);
                break;
            case "sendOffer":
                handleSendOffer(request, response);
                break;
            case "acceptOffer":
                handleAcceptOffer(request, response);
                break;
            case "rejectOffer":
                handleRejectOffer(request, response);
                break;
            case "markComplete":
                handleMarkComplete(request, response);
                break;
            case "submitRating":
                handleSubmitRating(request, response);
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
            case "viewRequests":
                handleViewRequests(request, response);
                break;
            case "myRequests":
                handleMyRequests(request, response);
                break;
            case "viewOffers":
                handleViewOffers(request, response);
                break;
            case "myTransactions":
                handleMyTransactions(request, response);
                break;
            case "viewRatings":
                handleViewRatings(request, response);
                break;
            default:
                ResponseUtil.sendError(response, "Invalid action: " + action);
        }
    }
    
    // ==================== POST HANDLERS ====================
    
    /**
     * Create service request
     * 
     * Parameters:
     * - title
     * - description
     * - categoryId
     * - location
     * - priority (LOW, MEDIUM, HIGH, URGENT)
     */
    private void handleCreateRequest(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // Check authentication
        if (!isLoggedIn(request)) {
            ResponseUtil.sendError(response, "Not authenticated. Please login");
            return;
        }
        
        Integer userId = getLoggedInUserId(request);
        
        try {
            // Get parameters
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String categoryIdParam = request.getParameter("categoryId");
            String location = request.getParameter("location");
            String priority = request.getParameter("priority");
            
            // Validate required fields
            if (title == null || description == null || categoryIdParam == null) {
                ResponseUtil.sendError(response, "Title, description, and category are required");
                return;
            }
            
            int categoryId = Integer.parseInt(categoryIdParam);
            
            // Set default priority if not provided
            if (priority == null || priority.isEmpty()) {
                priority = "MEDIUM";
            }
            
            // Create request
            int requestId = requestHandler.createRequest(userId, title, description, 
                                                        categoryId, location, priority);
            
            if (requestId > 0) {
                ResponseUtil.sendSuccess(response, "Request created successfully", 
                    new IdResponse(requestId));
            } else {
                ResponseUtil.sendError(response, "Failed to create request");
            }
            
        } catch (NumberFormatException e) {
            ResponseUtil.sendError(response, "Invalid category ID format");
        } catch (Exception e) {
            ResponseUtil.sendError(response, "Error: " + e.getMessage());
        }
    }
    
    /**
     * Close service request
     * 
     * Parameters:
     * - requestId
     */
    private void handleCloseRequest(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // Check authentication
        if (!isLoggedIn(request)) {
            ResponseUtil.sendError(response, "Not authenticated. Please login");
            return;
        }
        
        try {
            String requestIdParam = request.getParameter("requestId");
            
            if (requestIdParam == null) {
                ResponseUtil.sendError(response, "Request ID is required");
                return;
            }
            
            int requestId = Integer.parseInt(requestIdParam);
            boolean closed = requestHandler.closeRequest(requestId);
            
            if (closed) {
                ResponseUtil.sendSuccess(response, "Request closed successfully");
            } else {
                ResponseUtil.sendError(response, "Failed to close request");
            }
            
        } catch (NumberFormatException e) {
            ResponseUtil.sendError(response, "Invalid request ID format");
        } catch (Exception e) {
            ResponseUtil.sendError(response, "Error: " + e.getMessage());
        }
    }
    
    /**
     * Cancel service request
     * 
     * Parameters:
     * - requestId
     */
    private void handleCancelRequest(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // Check authentication
        if (!isLoggedIn(request)) {
            ResponseUtil.sendError(response, "Not authenticated. Please login");
            return;
        }
        
        try {
            String requestIdParam = request.getParameter("requestId");
            
            if (requestIdParam == null) {
                ResponseUtil.sendError(response, "Request ID is required");
                return;
            }
            
            int requestId = Integer.parseInt(requestIdParam);
            boolean cancelled = requestHandler.cancelRequest(requestId);
            
            if (cancelled) {
                ResponseUtil.sendSuccess(response, "Request cancelled successfully");
            } else {
                ResponseUtil.sendError(response, "Failed to cancel request");
            }
            
        } catch (NumberFormatException e) {
            ResponseUtil.sendError(response, "Invalid request ID format");
        } catch (Exception e) {
            ResponseUtil.sendError(response, "Error: " + e.getMessage());
        }
    }
    
    /**
     * Provider sends offer
     * 
     * Parameters:
     * - requestId
     * - message
     * - quotedPrice
     */
    private void handleSendOffer(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // Check authentication
        if (!isLoggedIn(request)) {
            ResponseUtil.sendError(response, "Not authenticated. Please login");
            return;
        }
        
        Integer providerId = getLoggedInUserId(request);
        
        try {
            // Get parameters
            String requestIdParam = request.getParameter("requestId");
            String message = request.getParameter("message");
            String quotedPriceParam = request.getParameter("quotedPrice");
            
            // Validate
            if (requestIdParam == null || quotedPriceParam == null) {
                ResponseUtil.sendError(response, "Request ID and quoted price are required");
                return;
            }
            
            int requestId = Integer.parseInt(requestIdParam);
            double quotedPrice = Double.parseDouble(quotedPriceParam);
            
            // Send offer
            int transactionId = transactionHandler.sendOffer(requestId, providerId, 
                                                            message, quotedPrice);
            
            if (transactionId > 0) {
                ResponseUtil.sendSuccess(response, "Offer sent successfully", 
                    new IdResponse(transactionId));
            } else {
                ResponseUtil.sendError(response, "Failed to send offer. You may have already applied");
            }
            
        } catch (NumberFormatException e) {
            ResponseUtil.sendError(response, "Invalid number format");
        } catch (Exception e) {
            ResponseUtil.sendError(response, "Error: " + e.getMessage());
        }
    }
    
    /**
     * Accept offer (CRITICAL!)
     * 
     * Parameters:
     * - requestId
     * - transactionId
     */
    private void handleAcceptOffer(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // Check authentication
        if (!isLoggedIn(request)) {
            ResponseUtil.sendError(response, "Not authenticated. Please login");
            return;
        }
        
        try {
            String requestIdParam = request.getParameter("requestId");
            String transactionIdParam = request.getParameter("transactionId");
            
            if (requestIdParam == null || transactionIdParam == null) {
                ResponseUtil.sendError(response, "Request ID and transaction ID are required");
                return;
            }
            
            int requestId = Integer.parseInt(requestIdParam);
            int transactionId = Integer.parseInt(transactionIdParam);
            
            // Accept offer (automatically rejects others and closes request)
            boolean accepted = transactionHandler.acceptOffer(requestId, transactionId);
            
            if (accepted) {
                ResponseUtil.sendSuccess(response, "Offer accepted successfully");
            } else {
                ResponseUtil.sendError(response, "Failed to accept offer");
            }
            
        } catch (NumberFormatException e) {
            ResponseUtil.sendError(response, "Invalid ID format");
        } catch (Exception e) {
            ResponseUtil.sendError(response, "Error: " + e.getMessage());
        }
    }
    
    /**
     * Reject offer
     * 
     * Parameters:
     * - transactionId
     */
    private void handleRejectOffer(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // Check authentication
        if (!isLoggedIn(request)) {
            ResponseUtil.sendError(response, "Not authenticated. Please login");
            return;
        }
        
        try {
            String transactionIdParam = request.getParameter("transactionId");
            
            if (transactionIdParam == null) {
                ResponseUtil.sendError(response, "Transaction ID is required");
                return;
            }
            
            int transactionId = Integer.parseInt(transactionIdParam);
            boolean rejected = transactionHandler.rejectOffer(transactionId);
            
            if (rejected) {
                ResponseUtil.sendSuccess(response, "Offer rejected");
            } else {
                ResponseUtil.sendError(response, "Failed to reject offer");
            }
            
        } catch (NumberFormatException e) {
            ResponseUtil.sendError(response, "Invalid transaction ID format");
        } catch (Exception e) {
            ResponseUtil.sendError(response, "Error: " + e.getMessage());
        }
    }
    
    /**
     * Mark transaction as completed
     * 
     * Parameters:
     * - transactionId
     */
    private void handleMarkComplete(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // Check authentication
        if (!isLoggedIn(request)) {
            ResponseUtil.sendError(response, "Not authenticated. Please login");
            return;
        }
        
        try {
            String transactionIdParam = request.getParameter("transactionId");
            
            if (transactionIdParam == null) {
                ResponseUtil.sendError(response, "Transaction ID is required");
                return;
            }
            
            int transactionId = Integer.parseInt(transactionIdParam);
            boolean completed = transactionHandler.markAsCompleted(transactionId);
            
            if (completed) {
                ResponseUtil.sendSuccess(response, "Work marked as completed");
            } else {
                ResponseUtil.sendError(response, "Failed to mark as completed");
            }
            
        } catch (NumberFormatException e) {
            ResponseUtil.sendError(response, "Invalid transaction ID format");
        } catch (Exception e) {
            ResponseUtil.sendError(response, "Error: " + e.getMessage());
        }
    }
    
    /**
     * Submit rating
     * 
     * Parameters:
     * - transactionId
     * - providerId
     * - businessId
     * - stars (1-5)
     * - review
     */
    private void handleSubmitRating(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // Check authentication
        if (!isLoggedIn(request)) {
            ResponseUtil.sendError(response, "Not authenticated. Please login");
            return;
        }
        
        Integer userId = getLoggedInUserId(request);
        
        try {
            // Get parameters
            String transactionIdParam = request.getParameter("transactionId");
            String providerIdParam = request.getParameter("providerId");
            String businessIdParam = request.getParameter("businessId");
            String starsParam = request.getParameter("stars");
            String review = request.getParameter("review");
            
            // Validate
            if (transactionIdParam == null || providerIdParam == null || 
                businessIdParam == null || starsParam == null) {
                ResponseUtil.sendError(response, "Transaction ID, provider ID, business ID, and stars are required");
                return;
            }
            
            int transactionId = Integer.parseInt(transactionIdParam);
            int providerId = Integer.parseInt(providerIdParam);
            int businessId = Integer.parseInt(businessIdParam);
            int stars = Integer.parseInt(starsParam);
            
            // Submit rating (automatically updates business rating)
            int ratingId = ratingHandler.submitRating(transactionId, userId, providerId, 
                                                     businessId, stars, review);
            
            if (ratingId > 0) {
                ResponseUtil.sendSuccess(response, "Rating submitted successfully", 
                    new IdResponse(ratingId));
            } else {
                ResponseUtil.sendError(response, "Failed to submit rating. You may have already rated this transaction");
            }
            
        } catch (NumberFormatException e) {
            ResponseUtil.sendError(response, "Invalid number format");
        } catch (Exception e) {
            ResponseUtil.sendError(response, "Error: " + e.getMessage());
        }
    }
    
    // ==================== GET HANDLERS ====================
    
    /**
     * View all open requests
     */
    private void handleViewRequests(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            List<ServiceRequest> requests = requestHandler.getOpenRequests();
            
            if (requests != null) {
                ResponseUtil.sendSuccess(response, "Requests retrieved", requests);
            } else {
                ResponseUtil.sendError(response, "Error retrieving requests");
            }
            
        } catch (Exception e) {
            ResponseUtil.sendError(response, "Error: " + e.getMessage());
        }
    }
    
    /**
     * View my requests
     */
    private void handleMyRequests(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // Check authentication
        if (!isLoggedIn(request)) {
            ResponseUtil.sendError(response, "Not authenticated. Please login");
            return;
        }
        
        Integer userId = getLoggedInUserId(request);
        
        try {
            List<ServiceRequest> requests = requestHandler.getMyRequests(userId);
            
            if (requests != null) {
                ResponseUtil.sendSuccess(response, "Your requests retrieved", requests);
            } else {
                ResponseUtil.sendError(response, "Error retrieving requests");
            }
            
        } catch (Exception e) {
            ResponseUtil.sendError(response, "Error: " + e.getMessage());
        }
    }
    
    /**
     * View all offers for a request
     * 
     * Parameters:
     * - requestId
     */
    private void handleViewOffers(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // Check authentication
        if (!isLoggedIn(request)) {
            ResponseUtil.sendError(response, "Not authenticated. Please login");
            return;
        }
        
        try {
            String requestIdParam = request.getParameter("requestId");
            
            if (requestIdParam == null) {
                ResponseUtil.sendError(response, "Request ID is required");
                return;
            }
            
            int requestId = Integer.parseInt(requestIdParam);
            List<ServiceTransaction> offers = transactionHandler.getOffersForRequest(requestId);
            
            if (offers != null) {
                ResponseUtil.sendSuccess(response, "Offers retrieved", offers);
            } else {
                ResponseUtil.sendSuccess(response, "Offers Not Found");
            }
            
        } catch (NumberFormatException e) {
            ResponseUtil.sendError(response, "Invalid request ID format");
        } catch (Exception e) {
            ResponseUtil.sendError(response, "Error: " + e.getMessage());
        }
    }
    
    /**
     * View my transactions (provider's applications)
     */
    private void handleMyTransactions(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // Check authentication
        if (!isLoggedIn(request)) {
            ResponseUtil.sendError(response, "Not authenticated. Please login");
            return;
        }
        
        Integer providerId = getLoggedInUserId(request);
        
        try {
            List<ServiceTransaction> transactions = transactionHandler.getMyTransactions(providerId);
            
            if (transactions != null) {
                ResponseUtil.sendSuccess(response, "Your transactions retrieved", transactions);
            } else {
                ResponseUtil.sendError(response, "Error retrieving transactions");
            }
            
        } catch (Exception e) {
            ResponseUtil.sendError(response, "Error: " + e.getMessage());
        }
    }
    
    /**
     * View ratings for a business
     * 
     * Parameters:
     * - businessId
     */
    private void handleViewRatings(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            String businessIdParam = request.getParameter("businessId");
            
            if (businessIdParam == null) {
                ResponseUtil.sendError(response, "Business ID is required");
                return;
            }
            
            int businessId = Integer.parseInt(businessIdParam);
            List<Rating> ratings = ratingHandler.getRatingsForBusiness(businessId);
            RatingStats stats = ratingHandler.getAverageRating(businessId);
            
            // Create response with both ratings and stats
            RatingsResponse ratingsResponse = new RatingsResponse(ratings, stats);
            
            if (ratings != null) {
                ResponseUtil.sendSuccess(response, "Ratings retrieved", ratingsResponse);
            } else {
                ResponseUtil.sendError(response, "Error retrieving ratings");
            }
            
        } catch (NumberFormatException e) {
            ResponseUtil.sendError(response, "Invalid business ID format");
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
    
    // ==================== RESPONSE CLASSES ====================
    
    /**
     * Simple ID response wrapper
     */
    class IdResponse {
        int id;
        
        IdResponse(int id) {
            this.id = id;
        }
    }
    
    /**
     * Ratings response with stats
     */
    class RatingsResponse {
        List<Rating> ratings;
        RatingStats stats;
        
        RatingsResponse(List<Rating> ratings, RatingStats stats) {
            this.ratings = ratings;
            this.stats = stats;
        }
    }
}
