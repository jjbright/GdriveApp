package com.app.gdrive;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.json.JSONException;

import com.google.api.services.drive.Drive;
 
public class DriveQuickStart {
	
	private static final GoogleDriveUtils gdUtils = new GoogleDriveUtils();
	
	private static CustomerManager cManager = new CustomerManager();
	
    public static void main(String... args) throws IOException, GeneralSecurityException, JSONException {
    	Customer cust = new Customer();
    	cust.setId(4);
    	cust.setAddress("404 Apple Street, Mugalivakkam, Chennai, 600125");
    	cust.setArt(false);
    	cust.setBusiness("Apple Cakes");
    	cust.setEmail("cake@apple.com");
    	cust.setMobile(9976954081L);
    	cust.setName("Apple Pie");
    	cust.setOrder(10);
    	
    	String custData = cManager.jsonCustomer(cust).toString();
    	System.out.println(custData);
    	Drive service = gdUtils.getDriveService();
    	String fileId = gdUtils.getFileId(service, "1G9vFaUCsPN6GomJRrK7vML9bSd5Wm63t", "customers.json");
		String nFileId = cManager.addCustomer(service, fileId, custData);
		System.out.println("File ID: " + nFileId);
		 
        
    }
}