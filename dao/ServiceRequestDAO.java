package com.serviceplatform.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.serviceplatform.models.ServiceRequest;
//import com.serviceplatform.enums.RequestStatus;
import com.serviceplatform.enums.ServiceRequestStatus;
import com.serviceplatform.enums.PriorityLevel;
import com.serviceplatform.queryexecutor.QueryExecutor;
import com.serviceplatform.querygenerator.QueryGenerator;
import com.serviceplatform.utils.DbConnection;

public class ServiceRequestDAO {
    
    /**
     * Insert new service request
     * Returns: Generated service_request_id
     */
    public int insert(ServiceRequest request) throws SQLException {
        String query = QueryGenerator.generateInsert(
            "Service_Request",
            "user_id, title, description, category_id, location, priority, status, is_active",
            8
        );
        
        int generatedRequestId = QueryExecutor.executeInsert(
            query,
            request.getUserId(),
            request.getTitle(),
            request.getDescription(),
            request.getCategoryId(),
            request.getLocation(),
            request.getPriority().name(),     // Enum → String
            request.getStatus().name(),       // Enum → String
            request.isActive()
        );
        
        return generatedRequestId;
    }
    
    /**
     * Find request by service_request_id
     * Returns: ServiceRequest object or null if not found
     */
    public ServiceRequest findById(int requestId) throws SQLException {
        String query = QueryGenerator.generateSelectByOne("Service_Request", "service_request_id");
        ResultSet rs = QueryExecutor.executeSelectOne(query, requestId);
        
        try {
            if (rs.next()) {  // ✅ Single record - use if
                return mapResultSetToServiceRequest(rs);
            }
            return null;
        } finally {  // ✅ Use finally, not catch
            DbConnection.closeResources(null, null, rs);
        }
    }
    
    /**
     * Find all requests by user_id
     * Returns: List of user's requests (ordered by newest first)
     */
    public List<ServiceRequest> findByUserId(int userId) throws SQLException {
        String query = QueryGenerator.generateSelectWithOrder(
            "Service_Request",
            "user_id",
            "created_at DESC"
        );
        
        ResultSet rs = QueryExecutor.executeSelectMultiple(query, userId);
        List<ServiceRequest> requests = new ArrayList<>();
        
        try {
            while (rs.next()) {  // ✅ Multiple records - use while
                ServiceRequest request = mapResultSetToServiceRequest(rs);
                requests.add(request);
            }
            return requests;
        } finally {
            DbConnection.closeResources(null, null, rs);
        }
    }
    
    /**
     * Find all requests by status
     * Returns: List of requests with given status (ordered by newest first)
     */
    public List<ServiceRequest> findByStatus(ServiceRequestStatus status) throws SQLException {
        String query = QueryGenerator.generateSelectWithOrder(
            "Service_Request",
            "status",
            "created_at DESC"
        );
        
        // ✅ Convert enum to String for query
        ResultSet rs = QueryExecutor.executeSelectMultiple(query, status.name());
        List<ServiceRequest> requests = new ArrayList<>();
        
        try {
            while (rs.next()) {
                ServiceRequest request = mapResultSetToServiceRequest(rs);
                requests.add(request);
            }
            return requests;
        } finally {
            DbConnection.closeResources(null, null, rs);
        }
    }
    
    /**
     * Update request status
     * Returns: true if successful
     */
    public boolean updateStatus(int requestId, ServiceRequestStatus status) throws SQLException {
        String query = QueryGenerator.generateUpdateWithTimestamp(
            "Service_Request",
            "status",
            "service_request_id"
        );
        
        // ✅ Convert enum to String for query
        return QueryExecutor.executeUpdate(query, status.name(), requestId);
    }
    
    /**
     * Helper: Convert ResultSet to ServiceRequest object
     * Expects rs.next() to have been called already
     */
    private static ServiceRequest mapResultSetToServiceRequest(ResultSet rs) throws SQLException {
        ServiceRequest request = new ServiceRequest();
        
        // Map all columns
        request.setServiceRequestId(rs.getInt("service_request_id"));
        request.setUserId(rs.getInt("user_id"));
        request.setTitle(rs.getString("title"));  // ✅ FIXED: Was "tilte"
        request.setDescription(rs.getString("description"));
        request.setCategoryId(rs.getInt("category_id"));
        request.setLocation(rs.getString("location"));
        
        // ✅ Convert String to Enum
        String priorityStr = rs.getString("priority");
        request.setPriorityLevel(PriorityLevel.valueOf(priorityStr));  // "HIGH" → PriorityLevel.HIGH
        
        // ✅ Convert String to Enum
        String statusStr = rs.getString("status");
        request.setStatus(ServiceRequestStatus.valueOf(statusStr));  // "OPEN" → RequestStatus.OPEN
        
        request.setActive(rs.getBoolean("is_active"));
        request.setCreatedAt(rs.getTimestamp("created_at"));
        request.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        return request;  // ✅ FIXED: Was missing return
    }
}