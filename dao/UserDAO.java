package com.serviceplatform.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

import com.serviceplatform.models.User;
import com.serviceplatform.queryexecutor.QueryExecutor;
import com.serviceplatform.querygenerator.QueryGenerator;
import com.serviceplatform.utils.DbConnection;

public class UserDAO {

    /**
     * Insert new user into database
     * Password should already be hashed before calling this
     */
    public int insert(User user) throws SQLException {
        String query = QueryGenerator.generateInsert(
            "CUser",
            "user_name, email, phone_number, password_hash, bio, provider_status, is_active",
            7
        );

        int generatedId = QueryExecutor.executeInsert(
            query,
            user.getUserName(),
            user.getEmail(),
            user.getPhoneNumber(),
            user.getPasswordHash(),
            user.getBio(),
            user.isProviderStatus(),
            user.isActive()
        );
        
        return generatedId;
    }

    /**
     * Find user by ID
     * Returns: User object or null if not found
     */
    public User findUserById(int userId) throws SQLException {
        String query = QueryGenerator.generateSelectByOne("CUser", "user_id");
        ResultSet rs = QueryExecutor.executeSelectOne(query, userId);

        try {
            if (rs.next()) {  
                return mapResultSetToUser(rs);
            }
            return null;  // User not found
        } finally {
            DbConnection.closeResources(null, null, rs);  // ✅ FIXED: Close resources
        }
    }

    /**
     * Find user by email
     * Returns: User object or null if not found
     */
    public User findUserByEmail(String email) throws SQLException {
        String query = QueryGenerator.generateSelectByOne("CUser", "email");
        ResultSet rs = QueryExecutor.executeSelectOne(query, email);

        try {
            if (rs.next()) {  
                return mapResultSetToUser(rs);
            }
            return null;  // User not found
        } finally {
            DbConnection.closeResources(null, null, rs);  
        }
    }

    /**
     * Update user profile (name, email, phone, bio)
     * Returns: true if successful
     */
    public boolean updateProfile(User user) throws SQLException {
        String query = QueryGenerator.generateUpdateMultiple(
            "CUser",
            "user_name, email, phone_number, bio",
            "user_id"
        );

        return QueryExecutor.executeUpdate(
            query,
            user.getUserName(),
            user.getEmail(),
            user.getPhoneNumber(),
            user.getBio(),
            user.getUserId()  // WHERE user_id = ?
        );
    }

    /**
     * Update provider status
     * Returns: true if successful
     */
    public boolean updateProviderStatus(int userId, boolean status) throws SQLException {
        String query = QueryGenerator.generateUpdateOne(
            "CUser",
            "provider_status",  // ✅ FIXED: Correct column name
            "user_id"
        );

        return QueryExecutor.executeUpdate(query, status, userId);  // ✅ FIXED: Correct parameter order
    }

    /**
     * Authenticate user (login)
     * Verifies email and password using BCrypt
     * Returns: User object if valid, null otherwise
     */
    public User authenticate(String email, String plainPassword) throws SQLException {
        String query = QueryGenerator.generateSelectByOne("CUser", "email");
        ResultSet rs = QueryExecutor.executeSelectOne(query, email);

        try {
            if (rs.next()) {
                User user = mapResultSetToUser(rs);
                
                // Verify password using BCrypt
                boolean passwordMatches = BCrypt.checkpw(plainPassword, user.getPasswordHash());

                if (passwordMatches) {
                    return user;  // ✅ Authentication successful
                }
            }
            return null;  // ❌ Authentication failed
        } finally {
            DbConnection.closeResources(null, null, rs);
        }
    }

    /**
     * Convert ResultSet row to User object
     * Expects rs.next() to have been called already
     */
    private static User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();

        user.setUserId(rs.getInt("user_id"));
        user.setUserName(rs.getString("user_name"));
        user.setEmail(rs.getString("email"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setBio(rs.getString("bio"));
        user.setProviderStatus(rs.getBoolean("provider_status"));
        user.setActive(rs.getBoolean("is_active"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));

        return user;
    }
}