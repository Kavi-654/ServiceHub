package com.serviceplatform.utils;

import org.mindrot.jbcrypt.BCrypt;
import com.serviceplatform.exceptions.*;
public class PasswordUtil {

	private PasswordUtil() {
		throw new AssertionError("Utility class cannot be instantiated!");
	}
	
	// to hash the password
	public static String hashPassword(String plainPassword) throws PasswordException 
	{
		   if(plainPassword==null || plainPassword.isEmpty())
		   {
			   throw new PasswordException("Plain password cannot be null or empty!");
		   }
		   
		   try
		   {
			   return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
		   }
		   catch(Exception e)
		   {
			   throw new PasswordException("Failed to hash password",e);
		   }
		
	}
	
	// to verify the password
	public static boolean verifyPassword(String plainPassword,String hashedPassword) throws PasswordException  
	{
		if(hashedPassword==null || hashedPassword.isEmpty()) 
		{
			throw new  PasswordException("Hashed Password cannot be null or empty!");
		}
		
		if(plainPassword==null || plainPassword.isEmpty())
		{
			throw new PasswordException("Plain password Cannot be null or empty!!");
		}
		
		try
		{
			return BCrypt.checkpw(plainPassword, hashedPassword);
		}
		catch(IllegalArgumentException e)
		{
			throw new PasswordException("Invalid Password hash Format",e);
			
		}
		catch(Exception e)
		{
			throw new PasswordException("Failed to verify password",e);
		}
	} 
	
//	public static void main(String[] args) throws PasswordException {
//	    // Test 1: Hash a password
//	    String plainPassword = "MySecurePass123";
//	    String hashed = PasswordUtil.hashPassword(plainPassword);
//	    System.out.println("Original: " + plainPassword);
//	    System.out.println("Hashed: " + hashed);
//	    
//	    // Test 2: Verify correct password
//	    boolean isValid = PasswordUtil.verifyPassword(plainPassword, hashed);
//	    System.out.println("Valid password? " + isValid);  // Should be true
//	    
//	    // Test 3: Verify wrong password
//	    boolean isInvalid = PasswordUtil.verifyPassword("WrongPassword", hashed);
//	    System.out.println("Wrong password? " + isInvalid);  // Should be false
//	    
//	    // Test 4: Same password hashed twice gives different hashes (salt)
//	    String hashed2 = PasswordUtil.hashPassword(plainPassword);
//	    System.out.println("\nSame password, different hash:");
//	    System.out.println("Hash 1: " + hashed);
//	    System.out.println("Hash 2: " + hashed2);
//	    System.out.println("But both verify: " + PasswordUtil.verifyPassword(plainPassword, hashed2));
//	}
	

	
	
	
	

}
