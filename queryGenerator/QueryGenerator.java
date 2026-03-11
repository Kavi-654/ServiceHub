package com.serviceplatform.querygenerator;

public class QueryGenerator {
    
    // ==================== INSERT QUERIES ====================
    
    /**
     * Generate INSERT query
     * Example: generateInsert("CUser", "user_name, email, password_hash", 3)
     * Returns: INSERT INTO CUser (user_name, email, password_hash) VALUES (?, ?, ?)
     */
    public static String generateInsert(String tableName, String columns, int numberOfValues) {
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < numberOfValues; i++) {
            placeholders.append("?");
            if (i < numberOfValues - 1) {
                placeholders.append(", ");
            }
        }
        return "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholders + ")";
    }
    
    // ==================== SELECT QUERIES ====================
    
    /**
     * Select all records from table
     * Example: generateSelectAll("Category")
     * Returns: SELECT * FROM Category
     */
    public static String generateSelectAll(String tableName) {
        return "SELECT * FROM " + tableName;
    }
    
    /**
     * Select by one condition
     * Example: generateSelectByOne("CUser", "user_id")
     * Returns: SELECT * FROM CUser WHERE user_id = ?
     */
    public static String generateSelectByOne(String tableName, String columnName) {
        return "SELECT * FROM " + tableName + " WHERE " + columnName + " = ?";
    }
    
    /**
     * Select by two conditions (AND)
     * Example: generateSelectByTwo("Transaction", "request_id", "provider_id")
     * Returns: SELECT * FROM Transaction WHERE request_id = ? AND provider_id = ?
     */
    public static String generateSelectByTwo(String tableName, String column1, String column2) {
        return "SELECT * FROM " + tableName + " WHERE " + column1 + " = ? AND " + column2 + " = ?";
    }
    
    /**
     * Select with ORDER BY
     * Example: generateSelectWithOrder("Service_Request", "user_id", "created_at DESC")
     * Returns: SELECT * FROM Service_Request WHERE user_id = ? ORDER BY created_at DESC
     */
    public static String generateSelectWithOrder(String tableName, String whereColumn, String orderBy) {
        if (whereColumn != null && !whereColumn.isEmpty()) {
            return "SELECT * FROM " + tableName + " WHERE " + whereColumn + " = ? ORDER BY " + orderBy;
        }
        return "SELECT * FROM " + tableName + " ORDER BY " + orderBy;
    }
    
    // ==================== UPDATE QUERIES ====================
    
    /**
     * Update one column
     * Example: generateUpdateOne("CUser", "user_name", "user_id")
     * Returns: UPDATE CUser SET user_name = ? WHERE user_id = ?
     */
    public static String generateUpdateOne(String tableName, String updateColumn, String whereColumn) {
        return "UPDATE " + tableName + " SET " + updateColumn + " = ? WHERE " + whereColumn + " = ?";
    }
    
    /**
     * Update multiple columns
     * Example: generateUpdateMultiple("CUser", "user_name, email, bio", "user_id")
     * Returns: UPDATE CUser SET user_name = ?, email = ?, bio = ? WHERE user_id = ?
     */
    public static String generateUpdateMultiple(String tableName, String updateColumns, String whereColumn) {
        String[] columns = updateColumns.split(",");
        StringBuilder setClause = new StringBuilder();
        
        for (int i = 0; i < columns.length; i++) {
            setClause.append(columns[i].trim()).append(" = ?");
            if (i < columns.length - 1) {
                setClause.append(", ");
            }
        }
        
        return "UPDATE " + tableName + " SET " + setClause + " WHERE " + whereColumn + " = ?";
    }
    
    /**
     * Update with timestamp
     * Example: generateUpdateWithTimestamp("Service_Request", "status", "service_request_id")
     * Returns: UPDATE Service_Request SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE service_request_id = ?
     */
    public static String generateUpdateWithTimestamp(String tableName, String updateColumn, String whereColumn) {
        return "UPDATE " + tableName + " SET " + updateColumn + " = ?, updated_at = CURRENT_TIMESTAMP WHERE " + whereColumn + " = ?";
    }
    
    /**
     * Update multiple columns with timestamp
     * Example: generateUpdateMultipleWithTimestamp("Business", "name, description, avg_rating", "business_id")
     * Returns: UPDATE Business SET name = ?, description = ?, avg_rating = ?, updated_at = CURRENT_TIMESTAMP WHERE business_id = ?
     */
    public static String generateUpdateMultipleWithTimestamp(String tableName, String updateColumns, String whereColumn) {
        String[] columns = updateColumns.split(",");
        StringBuilder setClause = new StringBuilder();
        
        for (int i = 0; i < columns.length; i++) {
            setClause.append(columns[i].trim()).append(" = ?");
            if (i < columns.length - 1) {
                setClause.append(", ");
            }
        }
        
        return "UPDATE " + tableName + " SET " + setClause + ", updated_at = CURRENT_TIMESTAMP WHERE " + whereColumn + " = ?";
    }
    
    /**
     * CRITICAL: Reject other transactions when one is accepted
     * Example: generateRejectOtherTransactions()
     * Returns: UPDATE Transaction SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE request_id = ? AND transaction_id != ?
     */
    public static String generateRejectOtherTransactions() {
        return "UPDATE Service_Transaction SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE request_id = ? AND transaction_id != ?";
    }
    
    // ==================== DELETE QUERIES ====================
    
    /**
     * Delete by one condition
     * Example: generateDelete("CUser", "user_id")
     * Returns: DELETE FROM CUser WHERE user_id = ?
     */
    public static String generateDelete(String tableName, String whereColumn) {
        return "DELETE FROM " + tableName + " WHERE " + whereColumn + " = ?";
    }
    
    // ==================== COUNT QUERIES ====================
    
    /**
     * Count all records
     * Example: generateCountAll("CUser")
     * Returns: SELECT COUNT(*) FROM CUser
     */
    public static String generateCountAll(String tableName) {
        return "SELECT COUNT(*) FROM " + tableName;
    }
    
    /**
     * Count with one condition
     * Example: generateCountByOne("Transaction", "request_id")
     * Returns: SELECT COUNT(*) FROM Transaction WHERE request_id = ?
     */
    public static String generateCountByOne(String tableName, String whereColumn) {
        return "SELECT COUNT(*) FROM " + tableName + " WHERE " + whereColumn + " = ?";
    }
    
    /**
     * Count with two conditions - CHECK IF PROVIDER ALREADY APPLIED
     * Example: generateCountByTwo("Transaction", "request_id", "provider_id")
     * Returns: SELECT COUNT(*) FROM Transaction WHERE request_id = ? AND provider_id = ?
     */
    public static String generateCountByTwo(String tableName, String column1, String column2) {
        return "SELECT COUNT(*) FROM " + tableName + " WHERE " + column1 + " = ? AND " + column2 + " = ?";
    }
    
    // ==================== AGGREGATE QUERIES ====================
    
    /**
     * Calculate average rating for business
     * Example: generateAverageRating("Rating", "no_of_stars", "business_id")
     * Returns: SELECT AVG(no_of_stars) as avg_rating, COUNT(*) as total_ratings FROM Rating WHERE business_id = ?
     */
    public static String generateAverageRating(String tableName, String avgColumn, String whereColumn) {
        return "SELECT AVG(" + avgColumn + ") as avg_rating, COUNT(*) as total_ratings FROM " + tableName + " WHERE " + whereColumn + " = ?";
    }
    
    // ==================== JOIN QUERIES ====================
    
    /**
     * CRITICAL: Get transaction details with provider and business info
     * This is used when user views all applications for their request
     * 
     * Returns: Full provider details including business profile
     */
    public static String generateTransactionWithProviderInfo() {
        return "SELECT t.transaction_id, t.request_id, t.provider_id, t.message, t.quoted_price, " +
               "t.status,t.is_active, t.created_at, t.updated_at, " +
               "u.user_name as provider_name, u.email as provider_email, u.phone_number as provider_phone, " +
               "b.business_id, b.name as business_name, b.description as business_description, " +
               "b.avg_rating, b.total_ratings " +
               "FROM Service_Transaction t " +
               "INNER JOIN CUser u ON t.provider_id = u.user_id " +
               "LEFT JOIN Business b ON b.owner_id = u.user_id " +
               "WHERE t.request_id = ?";
    }
    
    /**
     * Get service request with user details
     */
    public static String generateRequestWithUserInfo() {
        return "SELECT sr.*, u.user_name, u.email, u.phone_number, c.category_name " +
               "FROM Service_Request sr " +
               "INNER JOIN CUser u ON sr.user_id = u.user_id " +
               "INNER JOIN Category c ON sr.category_id = c.category_id " +
               "WHERE sr.service_request_id = ?";
    }
    
    /**
     * Get businesses by category with owner info
     */
    public static String generateBusinessesByCategory() {
        return "SELECT b.*, u.user_name as owner_name, u.email as owner_email, u.phone_number " +
               "FROM Business b " +
               "INNER JOIN CUser u ON b.owner_id = u.user_id " +
               "WHERE b.category_id = ? AND b.is_active = true " +
               "ORDER BY b.avg_rating DESC";
    }
    
    /**
     * Get ratings for a business with user details
     */
    public static String generateRatingsWithUserInfo() {
        return "SELECT r.*, u.user_name, u.email " +
               "FROM Rating r " +
               "INNER JOIN CUser u ON r.user_id = u.user_id " +
               "WHERE r.business_id = ? " +
               "ORDER BY r.created_at DESC";
    }
    
    /**
     * Authenticate user - check email and password
     */
    public static String generateAuthenticate() {
        return "SELECT * FROM CUser WHERE email = ? AND password_hash = ?";
    }
    
    /**
     * Check if rating already exists for transaction
     */
    public static String generateCheckRatingExists() {
        return "SELECT COUNT(*) FROM Rating WHERE transaction_id = ?";
    }
}