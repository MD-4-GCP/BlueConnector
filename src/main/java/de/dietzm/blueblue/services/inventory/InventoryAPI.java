package de.dietzm.blueblue.services.inventory;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;

import de.dietzm.blueblue.base.APIResult;
import de.dietzm.blueblue.base.APIResultBuilder;
import de.dietzm.blueblue.model.InventoryDocument;
import de.dietzm.blueblue.saptoolbox.DestinationManager;
import de.dietzm.blueblue.saptoolbox.JCOToolbox;
import spark.Request;
import spark.Response;

public class InventoryAPI {	


	public APIResult readInventoryDocumentsFromSAP(Request request, Response resp) throws Exception {
		
		String destname = request.params(":destname"); 
		
		String plant = request.queryParams("plant"); 
		String location = request.queryParams("location"); 
		String materialnumber = request.queryParams("material");
		String status = request.queryParams("counted");
		
		JCoDestination destination = DestinationManager.getInstance().getDestination(destname);
		JCoFunction function = destination.getRepository().getFunction("BAPI_MATPHYSINV_GETITEMS");
		
		//Selections
		if(plant!= null)
			JCOToolbox.addRangeTableEntryEQ(function, "PLANT_RA", plant);
		
		if(location != null) 
			JCOToolbox.addRangeTableEntryEQ(function, "STGE_LOC_RA", location);
			
		if(status != null) {
			if(status.equals("1") || status.equalsIgnoreCase("X") || status.equalsIgnoreCase("true"))
				status = "X";
			else 
				status = "";
			JCOToolbox.addRangeTableEntryEQ(function, "COUNT_STATUS_RA", status);
		}
		
		if(materialnumber != null) 
			JCOToolbox.addRangeTableEntryEQ(function, "MATERIAL_RA", materialnumber);
		
		
		function.execute(destination);
		
		
		//Handle exported Tables
		JCoTable sapHeaders = function.getTableParameterList().getTable("HEADERS");
		JCoTable sapItems = function.getTableParameterList().getTable("ITEMS");
		
		HashMap<String, InventoryDocument> headers = new HashMap<>();
		ArrayList<InventoryDocument> itemList = new ArrayList<InventoryDocument>();
		
		if(sapHeaders.getNumRows() >= 1) {
			do {
				InventoryDocument header = new InventoryDocument(); 
				header.setSAPDocumentId(sapHeaders.getString("PHYSINVENTORY"));
				header.setPlant(new Integer(sapHeaders.getString(("PLANT"))).intValue());
				header.setLocation(sapHeaders.getString("STGE_LOC"));
				
				headers.put(header.getSAPDocumentId(), header);
			}while(sapHeaders.nextRow());
		}
		
		if(sapItems.getNumRows() >= 1) {
			do {
				InventoryDocument item = new InventoryDocument();
				String headerID = sapItems.getString("PHYSINVENTORY");
				
				item.setItem(sapItems.getInt("ITEM"));
				item.setMaterialNumber(sapItems.getString("MATERIAL"));
				item.setUoM(sapItems.getString("BASE_UOM"));
				item.setInStock(sapItems.getDouble("BOOK_QTY"));
				item.setCount(sapItems.getDouble("QUANTITY"));				
				item.setSAPFiscalYear(sapItems.getInt("FISCALYEAR"));
				
				String counted = sapItems.getString("COUNTED");
				if(counted.equals("X")) {
					item.setCounted(true);
				} else {
					item.setCounted(false);
				}
				
				InventoryDocument header = headers.get(headerID);
				moveHeaderDataToItemEntry(header, item);
				
				itemList.add(item);
			}while(sapItems.nextRow());
		}
		
		InventoryDocument[] stockTakingList = itemList.toArray(new InventoryDocument[itemList.size()]);		
		return APIResultBuilder.success(stockTakingList);
		
	}
	
	public APIResult postInventoryCount(Request request, Response resp) throws Exception {
		
		String destname = request.params(":destname"); 
		
		JsonObject input = (JsonObject) new JsonParser().parse(request.body());
		String invdocNumber = input.get("SAPDocumentId").getAsString();
		String fiscalYear  = input.get("SAPFiscalYear").getAsString();
		JsonArray items = input.getAsJsonArray("items");
		
		JCoDestination destination = DestinationManager.getInstance().getDestination(destname);
		JCoFunction function = destination.getRepository().getFunction("BAPI_MATPHYSINV_COUNT");
		
		function.getImportParameterList().setValue("PHYSINVENTORY", invdocNumber);
		function.getImportParameterList().setValue("FISCALYEAR", fiscalYear);
		function.getImportParameterList().setValue("PERCENTAGE_VARIANCE", 100);
		
		JCOToolbox.enterJsonArrayToTable(function, "ITEMS", items);
				
		function.execute(destination);
		
		JsonArray resultDetails = JCOToolbox.convertTable(function, "RETURN");
		
		JCoFunction functionCommit = destination.getRepository().getFunction("BAPI_TRANSACTION_COMMIT");
		functionCommit.getImportParameterList().setValue("WAIT", "X");
		functionCommit.execute(destination);
		
		//JsonArray resultCommit = JCOToolbox.convertTable(function, "RETURN");
		
		
		return APIResultBuilder.success()
					.addData("ResultDetails", resultDetails);
	}



	private void moveHeaderDataToItemEntry(InventoryDocument header, InventoryDocument item) {
		item.setPlant(header.getPlant());
		item.setLocation(header.getLocation());
		item.setWarehouse(header.getWarehouse());
		item.setSAPDocumentId(header.getSAPDocumentId());
	}

	
}
