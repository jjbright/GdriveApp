package com.app.gdrive;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class GoogleDriveUtils {
	
	private static final String APPLICATION_NAME = "PrintEzy";
	 
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
 
    // Directory to store user credentials for this application.
    private static final java.io.File CREDENTIALS_FOLDER //
            = new java.io.File(System.getProperty("user.home"), "credentials");
 
    private static final String CLIENT_SECRET_FILE_NAME = "credentials-printezy.json";
 
    // Global instance of the scopes required by this PrintEzy. If modifying these
    // scopes, delete your previously saved credentials/ folder.
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    
    private static Drive _driveService;
    
	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {		 
        java.io.File clientSecretFilePath = new java.io.File(CREDENTIALS_FOLDER, CLIENT_SECRET_FILE_NAME); 
        if (!clientSecretFilePath.exists()) {
            throw new FileNotFoundException("Please copy " + CLIENT_SECRET_FILE_NAME + " to folder: " 
            		+ CREDENTIALS_FOLDER.getAbsolutePath());
        }
 
        // Load client secrets.
        InputStream in = new FileInputStream(clientSecretFilePath); 
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in)); 
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                clientSecrets, SCOPES).setDataStoreFactory(new FileDataStoreFactory(CREDENTIALS_FOLDER))
                        .setAccessType("offline").build(); 
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }
	
	
	public final Drive getDriveService() throws IOException, GeneralSecurityException {
        if (_driveService != null) {
            return _driveService;
        }
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = getCredentials(HTTP_TRANSPORT);
        _driveService = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
        return _driveService;
    }
	
	public String getFileId(Drive service, String folderId, String fileName) throws IOException
	{
		String fileId = null;
		FileList result = service.files().list() 
    			.setQ("'"+folderId+"' in parents and mimeType != 'application/vnd.google-apps.folder' and trashed = false")  
    			.setSpaces("drive").setFields("nextPageToken, files(id, name, parents)").execute();
    	List<File> files = result.getFiles();
    	if (files == null || files.size() == 0) {        	
        	throw new FileNotFoundException();
        } else {
        	for (File file : files) {
                if (file.getName().equalsIgnoreCase(fileName)) {
                	fileId = file.getId();
                	break;
                } else {
                	throw new FileNotFoundException();
                }
            }
        }
    	return fileId;
	}
}
