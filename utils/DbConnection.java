package com.serviceplatform.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbConnection {

	public DbConnection() {
		 
	}
	private static final String URL="jdbc:mysql://localhost:3306/service_hub";
	private static final String NAME="root";
	private static final String PASSWORD="Hello123@";
	private static final String DRIVER="com.mysql.cj.jdbc.Driver";
	
	static
	{
		try
		{
			Class.forName(DRIVER);
			System.out.print("My Sql Driver loaded SuccessFully!");
		}
		catch(ClassNotFoundException e)
		{
			System.out.println("MySQL Driver not found!");
			e.printStackTrace();
		}
	}
	

	public  static Connection getConnection() throws SQLException
	{
		Connection con=DriverManager.getConnection(URL,NAME,PASSWORD);
		return con;
	}
	
	public static void closeConnection(Connection con)
	{
		if(con!=null)
		{
		try {
			con.close();
			System.out.println("Connection Closed Successfully!!");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.print("Error Closing Connection!");
			e.printStackTrace();
		}
		}
	}
	
	public static void closeResources(Connection con,PreparedStatement ps,ResultSet rs)
	{
		
		if(rs!=null)
		{
			try
			{
				rs.close();
				System.out.println("Result set closed Successfully !");
				
			}
			catch(SQLException e)
			{
				System.out.print("Error Closing result set !");
				e.printStackTrace();
			}
		}
		
		if(ps!=null)
		{
			try
			{
				ps.close();
				System.out.println("Prepared Statement closed successfully!");
			}
			catch(Exception e)
			{
				System.out.println("Error Closing Prepared Statement!");
				e.printStackTrace();
			}
		}
		
		if(con!=null)
		{
		try {
			con.close();
			System.out.println("Connection Closed Successfully!!");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.print("Error Closing Connection!");
			e.printStackTrace();
		}
		}
		
		
		
		
	}
	
	public void closeResources(PreparedStatement ps,ResultSet rs)
	{
		closeResources(null,ps,rs);
	}
	
//	public static void main(String[] args) {
//        try {
//            Connection con = DbConnection.getConnection();
//            if (con != null) {
//                System.out.println("✅ Database connection successful!");
//                System.out.println("Connection details: " + con);
//                DbConnection.closeConnection(con);
//            }
//        } catch (SQLException e) {
//            System.err.println("❌ Database connection failed!");
//            e.printStackTrace();
//        }
//    }
	

}
