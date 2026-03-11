package com.serviceplatform.checkers;

public class UserChecker {

	public UserChecker() {
		// TODO Auto-generated constructor stub
	}
	
	public boolean IsValidEmail(String email)
	{
		if(email.isEmpty() || email.trim().isEmpty())
		{
			return false;
		}
		
		if(email.contains("@")&& email.contains(".com"))
		{
			return true;
		}
		return false;
	}
	
	public boolean isValidPassWord(String password)
	{
		int len=password.length();
		int small=0;
		int upper=0;
		int number=0;
		int spl=0;
		
		for(int i=0;i<password.length();i++)
		{
			if(Character.isLowerCase(password.charAt(i)))
			{
				small++;
			}
			else if(Character.isUpperCase(password.charAt(i)))
			{
				upper++;
			}
			else if(Character.isDigit(password.charAt(i)))
			{
				number++;
			}
			else
			{
				spl++;
			}
		}
		
		return len>=6 && small>=1 && upper>=1 && number>=1 && spl>=1;
	}

}
