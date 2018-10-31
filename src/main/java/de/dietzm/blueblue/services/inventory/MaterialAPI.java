package de.dietzm.blueblue.services.inventory;

import com.google.gson.JsonObject;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;

import de.dietzm.blueblue.base.APIResult;
import de.dietzm.blueblue.base.APIResultBuilder;
import de.dietzm.blueblue.model.Material;
import de.dietzm.blueblue.saptoolbox.DestinationManager;
import de.dietzm.blueblue.saptoolbox.JCOToolbox;
import spark.Request;
import spark.Response;
import spark.Spark;

public class MaterialAPI {

public APIResult readMaterialList(Request request, Response resp) throws Exception {
		
		String destname = request.params(":destname"); 
		
		String matnr = request.queryParams("material"); 
		String plant = request.queryParams("plant"); 
		String location = request.queryParams("location"); 
		
		JCoDestination destination = DestinationManager.getInstance().getDestination(destname);
		JCoFunction function = destination.getRepository().getFunction("BAPI_MATERIAL_GETLIST");
		
		function.getImportParameterList().setValue("MAXROWS", 10);
		
		if(matnr == null)
			matnr = "*";
		
		JCOToolbox.addRangeTableEntry(function, "MATNRSELECTION", "CP", "MATNR_", matnr);
		
		if(plant == null)
			Spark.halt(401, "query parameter plant is obligatory");
		
		JCOToolbox.addRangeTableEntry(function, "PLANTSELECTION", "EQ", "PLANT_", plant);

		if(location != null)
			JCOToolbox.addRangeTableEntry(function, "STORAGELOCATIONSELECT", "EQ", "STLOC_", plant);
		
		function.execute(destination);
		
		Material[] materialList = JCOToolbox.convertTableToObjectArray(function, "MATNRLIST", Material[].class);
		
		return APIResultBuilder.success(materialList);
	}
	
	public APIResult readMaterialDetails(Request request, Response resp) throws Exception {
		
		String destname = request.params(":destname"); 
		
		String matnr = request.queryParams("material"); 
		String plant = request.queryParams("plant"); 
		
		JCoDestination destination = DestinationManager.getInstance().getDestination(destname);
		JCoFunction function = destination.getRepository().getFunction("BAPI_MATERIAL_GET_DETAIL");
		
		if(matnr == null) {
			return APIResultBuilder.errorAndHalt(401, "Query paramter material is needed");
		}
		
		function.getImportParameterList().setValue("MATERIAL", matnr);
		
		
		if(plant != null)
			function.getImportParameterList().setValue("PLANT", plant);
		
				
		function.execute(destination);
		
		JsonObject materialGeneralData = JCOToolbox.convertExportParameter(function, "MATERIAL_GENERAL_DATA");
		JsonObject materialPlantData = JCOToolbox.convertExportParameter(function, "MATERIALPLANTDATA");
		
		
		return APIResultBuilder.success()
					.addData("GeneralMaterialData", materialGeneralData)
					.addData("PlantMaterialData", materialPlantData);
	}

}
