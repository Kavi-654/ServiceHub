package com.serviceplatform.handlers;

import java.sql.SQLException;
import java.util.List;

import com.serviceplatform.dao.ServiceTransactionDAO;
import com.serviceplatform.dao.ServiceRequestDAO;
import com.serviceplatform.enums.TransactionStatus;
import com.serviceplatform.enums.ServiceRequestStatus;
import com.serviceplatform.models.ServiceTransaction;

public class ServiceTransactionHandler {
    private ServiceTransactionDAO transactionDAO;
    private ServiceRequestDAO serviceRequestDAO;

    public ServiceTransactionHandler() {
        this.transactionDAO = new ServiceTransactionDAO();
        this.serviceRequestDAO = new ServiceRequestDAO();
    }

    /**
     * Provider sends offer (applies to request)
     * THIS IS THE MOST IMPORTANT METHOD!
     * 
     * @return transaction_id if successful, 0 if failed
     */
    public int sendOffer(int requestId, int providerId, String message, double quotedPrice) {
        try {
            // STEP 1: Check if provider already applied (prevent duplicate)
            boolean alreadyApplied = transactionDAO.hasProviderApplied(requestId, providerId);
            
            if (alreadyApplied) {
                System.err.println("Provider already applied to this request!");
                return 0;  // Or throw exception
            }

            // STEP 2: Create transaction object
            ServiceTransaction transaction = new ServiceTransaction();
            transaction.setRequestId(requestId);
            transaction.setProviderId(providerId);
            transaction.setMessage(message);
            transaction.setQuotedPrice(quotedPrice);
            transaction.setStatus(TransactionStatus.PENDING);  // ✅ Default PENDING
            transaction.setActive(true);                       // ✅ Default active

            // STEP 3: Insert transaction
            int transactionId = transactionDAO.insert(transaction);
            return transactionId;

        } catch (SQLException e) {
            System.err.println("Error in sendOffer(): " + e.getMessage());
            return 0;
        }
    }

    /**
     * Get all offers for a request WITH provider details
     * 
     * @return List of transactions with provider info
     */
    public List<ServiceTransaction> getOffersForRequest(int requestId) {
        try {
            List<ServiceTransaction> list = transactionDAO.findByRequestWithProviderInfo(requestId);
            if(!list.isEmpty())
            {
            return list;
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Error in getOffersForRequest(): " + e.getMessage());
            return null;
        }
    }

    /**
     * Accept one offer (CRITICAL WORKFLOW METHOD!)
     * 
     * Steps:
     * 1. Update accepted transaction to ACCEPTED
     * 2. Reject all other transactions
     * 3. Close the service request
     * 
     * @return true if successful
     */
    public boolean acceptOffer(int requestId, int transactionId) {
        try {
            // STEP 1: Update accepted transaction to ACCEPTED
            
            boolean updated = transactionDAO.updateStatus(transactionId, TransactionStatus.ACCEPTED);
            
            if (!updated) {
                System.err.println("Failed to update transaction status");
                return false;
            }

            // STEP 2: Reject all other transactions
            boolean rejected = transactionDAO.rejectOtherTransactions(requestId, transactionId);
            
            if (!rejected) {
                System.err.println("Failed to reject other transactions");
//                return false;
            }

            // STEP 3: Close the service request
            boolean closed = serviceRequestDAO.updateStatus(requestId, ServiceRequestStatus.CLOSED);
            
            if (!closed) {
                System.err.println("Failed to close service request");
                return false;
            }

            return true;  // ✅ All steps successful

        } catch (SQLException e) {
            System.err.println("Error in acceptOffer(): " + e.getMessage());
            return false;
        }
    }

    /**
     * Reject one offer
     * 
     * @return true if successful
     */
    public boolean rejectOffer(int transactionId) {
        try {
            boolean isRejected = transactionDAO.updateStatus(transactionId, TransactionStatus.REJECTED);
            return isRejected;
        } catch (SQLException e) {
            System.err.println("Error in rejectOffer(): " + e.getMessage());
            return false;
        }
    }

    /**
     * Mark transaction as completed
     * 
     * @return true if successful
     */
    public boolean markAsCompleted(int transactionId) {
        try {
            boolean isCompleted = transactionDAO.updateStatus(transactionId, TransactionStatus.COMPLETED);
            return isCompleted;
        } catch (SQLException e) {
            System.err.println("Error in markAsCompleted(): " + e.getMessage());
            return false;
        }
    }

    /**
     * Get provider's transactions (applications)
     * 
     * @return List of provider's transactions
     */
    public List<ServiceTransaction> getMyTransactions(int providerId) {
        try {
            List<ServiceTransaction> list = transactionDAO.findByProviderId(providerId);
            return list;
        } catch (SQLException e) {
            System.err.println("Error in getMyTransactions(): " + e.getMessage());
            return null;
        }
    }

    /**
     * Get transaction by ID
     * 
     * @return Transaction object or null
     */
    public ServiceTransaction getTransactionById(int transactionId) {
        try {
            return transactionDAO.findById(transactionId);
        } catch (SQLException e) {
            System.err.println("Error in getTransactionById(): " + e.getMessage());
            return null;
        }
    }
    
    
}