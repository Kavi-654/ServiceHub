package com.serviceplatform.exceptions;

public class PasswordException extends Exception {

	public PasswordException() {
		// TODO Auto-generated constructor stub
	}
	
	public PasswordException(String message)
	{
		super(message);
	}
	
	public PasswordException(String message,Throwable cause)
	{
		super(message,cause);
	}

}
