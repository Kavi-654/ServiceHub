package com.serviceplatform.utils;

import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServletResponse;

public class ResponseUtil {
    
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()  // ✅ Makes JSON readable
            .serializeNulls()      // ✅ Include null values
            .create();
    
    /**
     * Send success response
     */
    public static void sendSuccess(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        
        ErrorResponse responseData=new ErrorResponse(true,message);
        String json=gson.toJson(responseData);
        response.getWriter().write(json);
        }
    
    /**
     * Send success response with data
     */
    public static void sendSuccess(HttpServletResponse response, String message, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        
        ResponseData responseData = new ResponseData(true, message, data);
        String json = gson.toJson(responseData);
        response.getWriter().write(json);
    }
    
    /**
     * Send error response
     */
    public static void sendError(HttpServletResponse response, String error) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        
        ErrorResponse errorResponse = new ErrorResponse(false, error);
        String json = gson.toJson(errorResponse);
        response.getWriter().write(json);
    }
    
    /**
     * Standard response format
     */
    static class ResponseData {
        boolean success;
        String message;
        Object data;
        
        ResponseData(boolean success, String message, Object data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }
    }
    
    /**
     * Error response format
     */
    static class ErrorResponse {
        boolean success;
        String message;
        
        ErrorResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
}