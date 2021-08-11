package com.dotmarketing.portlets.workflows;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ar.com.osde.dotcms.external.services.IOsdeSecurityExternalService;
import ar.com.osde.dotcms.framework.resources.OsdeFrameworkServices;

public class ShowFileServlet extends HttpServlet {
	private static IOsdeSecurityExternalService osdeSecurityService;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {		
			
		 	String fileName = request.getParameter("name");
		 	
		 	String[] parts = fileName.split("\\.");
		 	String name = parts[0];
		 	String extension = parts[1];
	        
	        File file = new File(this.getOsdeSecurityService().getFilesUploadPath().concat("/").concat(fileName));
	        
	        if (extension.equals("pdf")) {
	        	response.setContentType("application/pdf");
	        } else if (extension.equals("jpg") || extension.equals("jepg") || extension.equals("png")) {
	        	response.setContentType("image/jpeg");	        	
	        }	 

	        response.addHeader("Content-Disposition", "inline; filename=" + fileName);
	        response.setContentLength((int) file.length());
	 
	        FileInputStream fileInputStream = new FileInputStream(file);
	        OutputStream responseOutputStream = response.getOutputStream();
	        int bytes;
	        while ((bytes = fileInputStream.read()) != -1) {
	            responseOutputStream.write(bytes);
	        }	    
	}
	
	public IOsdeSecurityExternalService getOsdeSecurityService() {
		if (osdeSecurityService == null) {
			this.setOsdeSecurityService(OsdeFrameworkServices.OsdeSecurityExternalService());
		}
		return osdeSecurityService;
	}

	public void setOsdeSecurityService(IOsdeSecurityExternalService osdeSecurityService) {
		ShowFileServlet.osdeSecurityService = osdeSecurityService;
	}


}