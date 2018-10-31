package de.dietzm.blueblue.services.admin;

import java.io.File;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;

import de.dietzm.blueblue.base.JSONTransformer;
import spark.RouteGroup;
import spark.Spark;

public class RFCCallerAPI implements RouteGroup{

	private static final String BASE_FOLDER = "destinations" + File.separator;
	
	@Override
	public void addRoutes() {
		Spark.post("/:destname/:rfcname", (request, response) -> {
			
			String destname = request.params(":destname"); 
			String rfcname = request.params(":rfcname"); 
			JsonObject inJson = (JsonObject) new JsonParser().parse(request.body());
			
			return callRFC(destname, rfcname, inJson);
			
		}, new JSONTransformer());
		
		
		Spark.get("/:destname/:rfcname", (request, response) -> {
			
			String destname = request.params(":destname"); 
			String rfcname = request.params(":rfcname"); 
			
			return describeRFC(destname, rfcname);
			
		}, new JSONTransformer());
		
	}

	
	public JCoParameterList callRFC(String destname, String rfcname, JsonObject inJson) throws JCoException{
		
		JCoDestination destination = JCoDestinationManager.getDestination(BASE_FOLDER + destname);
		JCoFunction function = destination.getRepository().getFunction(rfcname);
		
		JCoParameterList importList = function.getImportParameterList();
		importList.getMetaData();
		
		function.execute(destination);
		
		return function.getExportParameterList();
		
	}
	
	
	public Object describeRFC(String destname, String rfcname) throws JCoException{
		
		JCoDestination destination = JCoDestinationManager.getDestination(BASE_FOLDER + destname);
		JCoFunction function = destination.getRepository().getFunction(rfcname);
		
		//JCoParameterList importList = function.getImportParameterList();
		return function;
		
	}


	
	
	
}
