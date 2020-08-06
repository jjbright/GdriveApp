package com.app.gdrive;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

public class CustomerManager {
	
	public JSONObject jsonCustomer(Customer contact) throws JSONException
	{
		JSONObject customer = new JSONObject();
		customer.put("id", contact.getId());
		customer.put("mobile", contact.getMobile());
		customer.put("name", contact.getName());
		customer.put("business", contact.getBusiness());
		customer.put("address", contact.getAddress());
		customer.put("email", contact.getEmail());
		customer.put("art", contact.isArt());
		customer.put("orders", contact.getOrder());
		return customer;
	}
	
	public String addCustomer(Drive service, String fileId, String customerJSON) throws IOException 
	{
		File file = service.files().get(fileId).execute();    		
		File updateFile = new File();
		updateFile.setName(file.getName());
		BufferedWriter writer = new BufferedWriter(new FileWriter(updateFile.getName(), true));  
		writer.write(",");
		writer.newLine();
		writer.write(customerJSON);
		writer.close();
		java.io.File uFile = new java.io.File(updateFile.getName());
		FileContent mediaContent = new FileContent("application/json", uFile);
		File updatedFile = service.files().update(fileId, updateFile, mediaContent).execute();
		return updatedFile.getId();
	}
	
}
