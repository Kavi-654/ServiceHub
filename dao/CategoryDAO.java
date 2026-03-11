package com.serviceplatform.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.serviceplatform.models.Category;
import com.serviceplatform.models.User;
import com.serviceplatform.queryexecutor.QueryExecutor;
import com.serviceplatform.querygenerator.QueryGenerator;
import com.serviceplatform.utils.DbConnection;

public class CategoryDAO {

	public CategoryDAO() {
		// TODO Auto-generated constructor stub
	}
	
	public List<Category> findAll() throws SQLException {
        // STEP 1: Generate SELECT ALL query
        String query = QueryGenerator.generateSelectAll("Category");
        
        // STEP 2: Execute query
        ResultSet rs = QueryExecutor.executeSelectMultiple(query);
        
        // STEP 3: Create list to store categories
        List<Category> categories = new ArrayList<>();
        
        try {
            // STEP 4: Loop through all results
            while (rs.next()) {  
                Category category = mapResultSetToCategory(rs);
                categories.add(category);
            }
            return categories;
        }
        finally {
        	DbConnection.closeResources(null, null, rs);
        }
	}
	
	public Category findCategoryById(int categoryId) throws SQLException {
        String query = QueryGenerator.generateSelectByOne("Category", "category_id");
        ResultSet rs = QueryExecutor.executeSelectOne(query, categoryId);

        try {
            if (rs.next()) { 
                return mapResultSetToCategory(rs);
            }
            return null;  // User not found
        } finally {
            DbConnection.closeResources(null, null, rs);  // ✅ FIXED: Close resources
        }
    }
            
	
	public  static Category mapResultSetToCategory(ResultSet rs) throws SQLException
	{
		Category category=new Category();
		
		category.setCategoryId(rs.getInt("category_id"));
		category.setCategoryName(rs.getString("category_name"));
		category.setCategoryDescription(rs.getString("category_description"));
		
		return category;
	}

}
