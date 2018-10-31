package de.dietzm.blueblue.services.admin;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import com.google.gson.Gson;
import com.sap.conn.jco.JCoAttributes;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.ext.DestinationDataProvider;

import de.dietzm.blueblue.base.APIResult;
import de.dietzm.blueblue.base.APIResultBuilder;
import de.dietzm.blueblue.model.DestinationInfo;
import de.dietzm.blueblue.saptoolbox.DestinationManager;
import spark.Request;
import spark.Response;
import spark.Spark;

public class DestinationAPI {

	private static final String DESTINATION_FOLDER = "destinations";
	private static final String LOCAL_FOLDER = DESTINATION_FOLDER + File.separator;
	

	public APIResult readDestinationList(Request request, Response response) throws Exception {
		
		if(!new File(LOCAL_FOLDER).exists())
			new File(LOCAL_FOLDER).mkdirs();
		
		ArrayList<String> fileList = new ArrayList<>();
		
		DestinationManager.getInstance().syncDestinationsList();			
		
		
		File[] files = new File(LOCAL_FOLDER).listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if(pathname.getName().endsWith(".jcoDestination")) 
					return true;
				return false;
			}
		});
		
		if(files != null) {		
			for (int i = 0; i < files.length; i++) {
				fileList.add(files[i].getName().replaceAll(".jcoDestination", ""));
			}			
		}
		
	    return APIResultBuilder.success(fileList.toArray(new String[fileList.size()]));
	}
	
	
	
	public APIResult testDestination(Request request, Response response) throws Exception {
		String name = request.params(":destname"); 
		
		if(new File(LOCAL_FOLDER + name + ".jcoDestination").exists()) {
		
		JCoDestination destination = JCoDestinationManager.getDestination(LOCAL_FOLDER + name);
		JCoAttributes att = destination.getAttributes();
		
		HashMap<String, String> result = new HashMap<>();
		result.put("Host", att.getPartnerHost());
		result.put("SYSID", att.getSystemID());
		result.put("Instance", att.getSystemNumber());
		result.put("Client", att.getClient());
		
		return APIResultBuilder.success(result);
		
		} else {
			Spark.halt(404, "Destination doesn't exist");
			return null;
		}
	}
	
	public APIResult createDestination(Request request, Response response) throws Exception {
		
		if(!new File(LOCAL_FOLDER).exists())
			new File(LOCAL_FOLDER).mkdirs();
		
		Gson gson = new Gson();
		
		String name = request.params(":destname"); 
		DestinationInfo dest = gson.fromJson(request.body(), DestinationInfo.class);
		dest.setDestinationName(name);
		
		Properties connectProperties = new Properties();

		connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, dest.getHost());
		connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, dest.getInstance());
		connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, dest.getClient());
		connectProperties.setProperty(DestinationDataProvider.JCO_USER, dest.getUser());
		connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, dest.getPass());
		connectProperties.setProperty(DestinationDataProvider.JCO_LANG, dest.getLanguage());

		File destCfg = new File(LOCAL_FOLDER + dest.getDestinationName() + ".jcoDestination");

		try { 
			
			FileOutputStream fos = new FileOutputStream(destCfg, false);
			connectProperties.store(fos, dest.getDescription());
			fos.close();
			
			DestinationManager.getInstance().syncDestinationsList();
			
		} catch (Exception e) {
			throw new RuntimeException("Unable to create the destination files", e);
		}
		
		 return APIResultBuilder.successMessage(new String(name + " created"));

	}
	
	public static Object getDestination(Request req, Response resp) {
		return "";
	}



}
