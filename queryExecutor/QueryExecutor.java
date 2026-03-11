package com.serviceplatform.queryexecutor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;

import com.serviceplatform.utils.DbConnection;

public class QueryExecutor {

    /**
     * Execute SELECT query that returns ONE record
     * Used for: findById(), findByEmail(), etc.
     * Returns: ResultSet (caller must close it)
     */
    public static ResultSet executeSelectOne(String query, Object... params) throws SQLException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        try {
            con = DbConnection.getConnection();
            pst = con.prepareStatement(query);
            setParameters(pst, params);
            rs = pst.executeQuery();
            return rs;
        } catch (SQLException e) {
            // Close resources if error occurs
            DbConnection.closeResources(con, pst, rs);
            throw new SQLException("Error executing SELECT query: " + e.getMessage(), e);
        }
    }
    
    /**
     * Execute SELECT query that returns MULTIPLE records
     * Used for: findAll(), findByStatus(), etc.
     * Returns: ResultSet (caller must close it)
     */
    public static ResultSet executeSelectMultiple(String query, Object... params) throws SQLException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        try {
            con = DbConnection.getConnection();
            pst = con.prepareStatement(query);
            setParameters(pst, params);
            rs = pst.executeQuery();
            return rs;
        } catch (SQLException e) {
            DbConnection.closeResources(con, pst, rs);
            throw new SQLException("Error executing SELECT query: " + e.getMessage(), e);
        }
    }
    
    /**
     * Execute INSERT query and return generated ID
     * Used for: Creating new user, request, transaction, etc.
     * Returns: Generated primary key (int)
     */
    public static int executeInsert(String query, Object... params) throws SQLException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet generatedKeys = null;
        
        try {
            con = DbConnection.getConnection();
            // IMPORTANT: Request generated keys
            pst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            setParameters(pst, params);
            
            int rowsAffected = pst.executeUpdate();
            
            if (rowsAffected > 0) {
                generatedKeys = pst.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return generated ID
                }
            }
            
            throw new SQLException("Insert failed, no rows affected");
            
        } catch (SQLException e) {
            throw new SQLException("Error executing INSERT query: " + e.getMessage(), e);
        } finally {
            // Always close resources
            DbConnection.closeResources(con, pst, generatedKeys);
        }
    }
    
    /**
     * Execute UPDATE query
     * Used for: Updating status, profile, ratings, etc.
     * Returns: true if successful, false if no rows affected
     */
    public static boolean executeUpdate(String query, Object... params) throws SQLException {
        Connection con = null;
        PreparedStatement pst = null;
        
        try {
            con = DbConnection.getConnection();
            pst = con.prepareStatement(query);
            setParameters(pst, params);  
            
            int rowsAffected = pst.executeUpdate();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            throw new SQLException("Error executing UPDATE query: " + e.getMessage(), e);
        } finally {
            DbConnection.closeResources(con, pst, null);
        }
    }
    
    /**
     * Execute DELETE query
     * Used for: Deleting records (soft delete preferred)
     * Returns: true if successful, false if no rows deleted
     */
    public static boolean executeDelete(String query, Object... params) throws SQLException {
        Connection con = null;
        PreparedStatement pst = null;
        
        try {
            con = DbConnection.getConnection();
            pst = con.prepareStatement(query);
            setParameters(pst, params);  
            
            int rowsAffected = pst.executeUpdate();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            throw new SQLException("Error executing DELETE query: " + e.getMessage(), e);
        } finally {
            DbConnection.closeResources(con, pst, null);
        }
    }
    
    /**
     * Execute COUNT query
     * Used for: Checking duplicates, counting records
     * Returns: count (int)
     */
    public static int executeCount(String query, Object... params) throws SQLException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        try {
            con = DbConnection.getConnection();
            pst = con.prepareStatement(query);
            setParameters(pst, params);  
            
            rs = pst.executeQuery();  
            
            if (rs.next()) {  
                return rs.getInt(1);
            }
            
            return 0;  // No results
            
        } catch (SQLException e) {
            throw new SQLException("Error executing COUNT query: " + e.getMessage(), e);
        } finally {
            DbConnection.closeResources(con, pst, rs);
        }
    }
    
    /**
     * Helper method: Set parameters in PreparedStatement
     * Handles different data types (String, int, boolean, etc.)
     */
    private static void setParameters(PreparedStatement pst, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            
            if (param instanceof String) {
                pst.setString(i + 1, (String) param);
            } else if (param instanceof Integer) {
                pst.setInt(i + 1, (Integer) param);
            } else if (param instanceof Long) {
                pst.setLong(i + 1, (Long) param);
            } else if (param instanceof Double) {
                pst.setDouble(i + 1, (Double) param);
            } else if (param instanceof Boolean) {
                pst.setBoolean(i + 1, (Boolean) param);
            } else if (param instanceof java.util.Date) {
                pst.setTimestamp(i + 1, new Timestamp(((java.util.Date) param).getTime()));
            } else if (param == null) {
                pst.setNull(i + 1, Types.NULL);
            } else {
                pst.setString(i + 1, param.toString());
            }
        }
    }
}