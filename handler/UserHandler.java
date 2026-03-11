package com.serviceplatform.handlers;

import java.sql.SQLException;

import com.serviceplatform.checkers.UserChecker;
import com.serviceplatform.dao.UserDAO;
import com.serviceplatform.exceptions.PasswordException;
import com.serviceplatform.models.User;
import com.serviceplatform.utils.PasswordUtil;

public class UserHandler {
    private UserDAO userDao;
    private UserChecker userChecker;

    public UserHandler() {
        this.userDao = new UserDAO();
        this.userChecker = new UserChecker();
    }

    /**
     * Register new user
     * 
     * @return user_id if successful, 0 if failed
     * @throws PasswordException if password validation fails
     */
    public int registerUser(String userName, String email, String phoneNumber, 
                           String plainPassword, String bio) throws PasswordException {
        // Validate email
        if (!userChecker.IsValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format"); 
        }

        // Validate password
        if (!userChecker.isValidPassWord(plainPassword)) {
            throw new IllegalArgumentException("Invalid password format"); 
        }

        // Hash password
        String hashPassword = PasswordUtil.hashPassword(plainPassword);

        // Create user object
        User user = new User();
        user.setUserName(userName);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setPasswordHash(hashPassword);
        user.setBio(bio);
        user.setProviderStatus(false);  
        user.setActive(true);           
        try {
            int generatedId = userDao.insert(user);
            return generatedId;  // ✅ Simplified (0 or valid ID)
        } catch (SQLException e) {
            System.err.println("Error in registerUser(): " + e.getMessage());  
            throw new RuntimeException("Failed to register user", e);  
        }
    }

    /**
     * Login user
     * 
     * @return User object if successful, null if failed
     */
    public User loginUser(String email, String plainPassword) {
        // Validate email
        if (!userChecker.IsValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // Validate password
        if (!userChecker.isValidPassWord(plainPassword)) {
            throw new IllegalArgumentException("Invalid password format");
        }

        try {
            User loggedUser = userDao.authenticate(email, plainPassword);
            return loggedUser;
        } catch (Exception e) {
            System.err.println("Error in loginUser(): " + e.getMessage());
            return null;
        }
    }

    /**
     * Get user by ID
     * 
     * @return User object or null if not found
     */
    public User getUserById(int userId) throws SQLException { 
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        try {
            User user = userDao.findUserById(userId);
            return user;
        } catch (SQLException e) {
            System.err.println("Error in getUserById(): " + e.getMessage());
            throw e;  
        }
    }

    /**
     * Update user profile
     * 
     * @return true if successful, false otherwise
     */
    public boolean updateProfile(int userId, String userName, String email, 
                                String phoneNumber, String bio) {
        try {
            User user = userDao.findUserById(userId);
            
            if (user == null) {  // ✅ ADDED: Null check
                System.err.println("User not found with ID: " + userId);
                return false;
            }
            
            user.setUserName(userName);
            user.setEmail(email);
            user.setPhoneNumber(phoneNumber);
            user.setBio(bio);
            
            boolean update = userDao.updateProfile(user);
            return update;
        } catch (SQLException e) {
            System.err.println("Error in updateProfile(): " + e.getMessage());
            return false;
        }
    }

    /**
     * Make user a provider
     * 
     * @return true if successful, false otherwise
     */
    public boolean becomeProvider(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        
        try {
            boolean update = userDao.updateProviderStatus(userId, true);
            return update;
        } catch (SQLException e) {
            System.err.println("Error in becomeProvider(): " + e.getMessage());
            return false;
        }
    }
  
}