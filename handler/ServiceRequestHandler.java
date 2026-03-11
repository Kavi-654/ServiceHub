package com.serviceplatform.handlers;

import java.sql.SQLException;
import java.util.List;

import com.serviceplatform.dao.ServiceRequestDAO;

import com.serviceplatform.enums.ServiceRequestStatus;
import com.serviceplatform.enums.PriorityLevel;  // ✅ ADDED
import com.serviceplatform.models.ServiceRequest;

public class ServiceRequestHandler {
    private ServiceRequestDAO serviceReqDAO;

    public ServiceRequestHandler() {
        this.serviceReqDAO = new ServiceRequestDAO();
    }

    /**
     * Create new service request
     * 
     * @return request_id if successful, 0 if failed
     */
    public int createRequest(int userId, String title, String description, 
                            int categoryId, String location, String priority) {
        // Validate user ID
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        try {
            ServiceRequest sr = new ServiceRequest();
            sr.setUserId(userId);
            sr.setTitle(title);
            sr.setDescription(description);
            sr.setCategoryId(categoryId);
            sr.setLocation(location);
            sr.setPriorityLevel(PriorityLevel.valueOf(priority));  // ✅ FIXED
            sr.setStatus(ServiceRequestStatus.OPEN);      // ✅ ADDED: Default OPEN
            sr.setActive(true);                    // ✅ ADDED: Default active
            
            int generatedId = serviceReqDAO.insert(sr);
            return generatedId;
        } catch (SQLException e) {
            System.err.println("Error in createRequest(): " + e.getMessage());
            return 0;
        }
    }

    /**
     * Get all open requests
     * 
     * @return List of open requests
     */
    public List<ServiceRequest> getOpenRequests() {  // ✅ FIXED: Method name
        try {
            List<ServiceRequest> list = serviceReqDAO.findByStatus(ServiceRequestStatus.OPEN);
            return list;
        } catch (SQLException e) {
            System.err.println("Error in getOpenRequests(): " + e.getMessage());
            return null;
        }
    }

    /**
     * Get user's requests
     * 
     * @return List of user's requests
     */
    public List<ServiceRequest> getMyRequests(int userId) {  
        try {
            List<ServiceRequest> list = serviceReqDAO.findByUserId(userId);
            return list;
        } catch (SQLException e) {
            System.err.println("Error in getMyRequests(): " + e.getMessage());
            return null;
        }
    }

    /**
     * Get request by ID
     * 
     * @return ServiceRequest object or null
     */
    public ServiceRequest getRequestById(int requestId) {
        try {
            ServiceRequest res = serviceReqDAO.findById(requestId);
            return res;
        } catch (SQLException e) {
            System.err.println("Error in getRequestById(): " + e.getMessage());
            return null;
        }
    }

    /**
     * Close request
     * 
     * @return true if successful
     */
    public boolean closeRequest(int requestId) {
        try {
            boolean isClosed = serviceReqDAO.updateStatus(requestId, ServiceRequestStatus.CLOSED);
            return isClosed;
        } catch (SQLException e) {
            System.err.println("Error in closeRequest(): " + e.getMessage());
            return false;
        }
    }

    /**
     * Cancel request
     * 
     * @return true if successful
     */
    public boolean cancelRequest(int requestId) {
        try {
            boolean isCancelled = serviceReqDAO.updateStatus(requestId, ServiceRequestStatus.CANCELLED);
            return isCancelled;
        } catch (SQLException e) {
            System.err.println("Error in cancelRequest(): " + e.getMessage());
            return false;
        }
    }
    
   
}