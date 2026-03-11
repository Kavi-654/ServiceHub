package com.serviceplatform.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.serviceplatform.models.ServiceTransaction;
import com.serviceplatform.enums.TransactionStatus;
import com.serviceplatform.queryexecutor.QueryExecutor;
import com.serviceplatform.querygenerator.QueryGenerator;
import com.serviceplatform.utils.DbConnection;

public class ServiceTransactionDAO {
    
    /**
     * Insert new transaction (Provider applies to request)
     * Returns: Generated transaction_id
     * 
     * IMPORTANT: Check hasProviderApplied() before calling this!
     */
    public int insert(ServiceTransaction transaction) throws SQLException {
        String query = QueryGenerator.generateInsert(
            "Service_Transaction",
            "request_id, provider_id, message, quoted_price, status, is_active",
            6
        );
        
        int generatedTransactionId = QueryExecutor.executeInsert(
            query,
            transaction.getRequestId(),
            transaction.getProviderId(),
            transaction.getMessage(),
            transaction.getQuotedPrice(),
            transaction.getStatus().name(),  // Enum → String
            transaction.isActive()
        );
        
        return generatedTransactionId;
    }
    
    /**
     * Find transaction by ID
     * Returns: Transaction object or null if not found
     */
    public ServiceTransaction findById(int transactionId) throws SQLException {
        String query = QueryGenerator.generateSelectByOne("Service_Transaction", "transaction_id");
        ResultSet rs = QueryExecutor.executeSelectOne(query, transactionId);
        
        try {
            if (rs.next()) {
                return mapResultSetToTransaction(rs);
            }
            return null;
        } finally {
            DbConnection.closeResources(null, null, rs);
        }
    }
    
    /**
     * Find all transactions for a request (simple version)
     * Returns: List of transactions
     */
    public List<ServiceTransaction> findByRequestId(int requestId) throws SQLException {
        String query = QueryGenerator.generateSelectByOne("Service_Transaction", "request_id");
        ResultSet rs = QueryExecutor.executeSelectMultiple(query, requestId);
        
        List<ServiceTransaction> transactions = new ArrayList<>();
        
        try {
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
            return transactions;
        } finally {
            DbConnection.closeResources(null, null, rs);
        }
    }
    
    /**
     * Find all transactions with FULL provider details
     * THIS IS THE MOST IMPORTANT METHOD!
     * Used when user views all applications for their request
     * Returns: List of transactions with provider name, email, business info
     */
    public List<ServiceTransaction> findByRequestWithProviderInfo(int requestId) throws SQLException {
        String query = QueryGenerator.generateTransactionWithProviderInfo();
        ResultSet rs = QueryExecutor.executeSelectMultiple(query, requestId);
        
        List<ServiceTransaction> transactions = new ArrayList<>();
        
        try {
            while (rs.next()) {
                ServiceTransaction transaction = mapResultSetToTransaction(rs);
                
                // If your Transaction model has additional fields for provider info:
                // transaction.setProviderName(rs.getString("provider_name"));
                // transaction.setProviderEmail(rs.getString("provider_email"));
                // transaction.setProviderPhone(rs.getString("provider_phone"));
                // transaction.setBusinessId(rs.getInt("business_id"));
                // transaction.setBusinessName(rs.getString("business_name"));
                // transaction.setBusinessDescription(rs.getString("business_description"));
                // transaction.setAvgRating(rs.getDouble("avg_rating"));
                // transaction.setTotalRatings(rs.getInt("total_ratings"));
                
                transactions.add(transaction);
            }
            return transactions;
        } finally {
            DbConnection.closeResources(null, null, rs);
        }
    }
    
    /**
     * Check if provider already applied to this request
     * Prevents duplicate applications
     * Returns: true if already applied, false otherwise
     */
    public boolean hasProviderApplied(int requestId, int providerId) throws SQLException {
        String query = QueryGenerator.generateCountByTwo("Service_Transaction", "request_id", "provider_id");
        int count = QueryExecutor.executeCount(query, requestId, providerId);
        
        return count > 0;
    }
    
    /**
     * Update transaction status
     * Used for: Accept, Reject, Complete
     * Returns: true if successful
     */
    public boolean updateStatus(int transactionId, TransactionStatus status) throws SQLException {
        String query = QueryGenerator.generateUpdateWithTimestamp(
            "Service_Transaction",
            "status",
            "transaction_id"
        );
        
        return QueryExecutor.executeUpdate(query, status.name(), transactionId);
    }
    
    /**
     * CRITICAL: Reject all OTHER transactions when one is accepted
     * When user accepts Provider B, this automatically rejects Provider A and C
     * Returns: true if successful
     */
    public boolean rejectOtherTransactions(int requestId, int acceptedTransactionId) throws SQLException {
        String query = QueryGenerator.generateRejectOtherTransactions();
        // Query: UPDATE Transaction SET status = ?, updated_at = CURRENT_TIMESTAMP 
        //        WHERE request_id = ? AND transaction_id != ?
        
        return QueryExecutor.executeUpdate(
            query,
            TransactionStatus.REJECTED.name(),  // Set status to REJECTED
            requestId,                          // For this request
            acceptedTransactionId               // Except this one (the accepted one)
        );
    }
    
    /**
     * Find all transactions by provider ID
     * Provider can see all their applications
     * Returns: List of transactions (ordered by newest first)
     */
    public List<ServiceTransaction> findByProviderId(int providerId) throws SQLException {
        String query = QueryGenerator.generateSelectWithOrder(
            "Service_Transaction",
            "provider_id",
            "created_at DESC"
        );
        
        ResultSet rs = QueryExecutor.executeSelectMultiple(query, providerId);
        List<ServiceTransaction> transactions = new ArrayList<>();
        
        try {
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
            return transactions;
        } finally {
            DbConnection.closeResources(null, null, rs);
        }
    }
    
    /**
     * Helper: Convert ResultSet to Transaction object
     * Expects rs.next() to have been called already
     */
    private ServiceTransaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        ServiceTransaction transaction = new ServiceTransaction();
        
        // Map all columns from Transaction table
        transaction.setTransactionId(rs.getInt("transaction_id"));
        transaction.setRequestId(rs.getInt("request_id"));
        transaction.setProviderId(rs.getInt("provider_id"));
        transaction.setMessage(rs.getString("message"));
        transaction.setQuotedPrice(rs.getDouble("quoted_price"));
        
        // Convert String to Enum
        String statusStr = rs.getString("status");
        transaction.setStatus(TransactionStatus.valueOf(statusStr));
        
        transaction.setActive(rs.getBoolean("is_active"));
        transaction.setCreatedAt(rs.getTimestamp("created_at"));
        transaction.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        return transaction;
    }
}