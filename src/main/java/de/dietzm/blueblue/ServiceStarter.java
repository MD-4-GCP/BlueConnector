package de.dietzm.blueblue;


import de.dietzm.blueblue.auth.AuthenticationManager;
import de.dietzm.blueblue.base.APIResultBuilder;
import de.dietzm.blueblue.base.HealthCheckAPI;
import de.dietzm.blueblue.base.JSONTransformer;
import de.dietzm.blueblue.services.admin.DestinationAPI;
import de.dietzm.blueblue.services.admin.RFCCallerAPI;
import de.dietzm.blueblue.services.inventory.InventoryAPI;
import de.dietzm.blueblue.services.inventory.MaterialAPI;
import spark.Spark;


/**
 * Hello world!
 *
 */
public class ServiceStarter 
{
    public static void main( String[] args ) throws Exception
    {   
        Spark.port(8080);
        Spark.init();
        
        AuthenticationManager authMan = new AuthenticationManager();
      
        Spark.path("health", new HealthCheckAPI());
        
        Spark.path("/api", () -> {
        	  
        	//Check Authorization
            Spark.before("/*", (q, a) -> {
            	a.header("Content-Type", "application/json");
            	if(!authMan.isAuthorized(q)) {
        			Spark.halt(401, "Not authorized for API");
        		}
        	});
            
            //Set Content Type JSON for all API Calls
            Spark.after("/*", (q, a) -> {
            	a.header("Content-Type", "application/json");
        	});
            
            
        	Spark.get("/welcome", (request, response) -> {
                return APIResultBuilder.successMessage("Welcome");
            }, new JSONTransformer());
        	
        	
        	Spark.path("/admin", () -> {
        		
        		Spark.before("/*", (q, a) -> {
        			if(!authMan.isAuthorizedFor(q, "admin")) {
            			Spark.halt(401, "Not authorized for Admin API");
            		}
            	});
        		
        		Spark.path("/destination", () -> {
        			DestinationAPI destAPI = new DestinationAPI();
        			Spark.get("", destAPI::readDestinationList, new JSONTransformer());
        			Spark.post("/:destname", destAPI::createDestination, new JSONTransformer());
        			Spark.get("/test/:destname", destAPI::testDestination, new JSONTransformer());
        			
        		});
        	});
        	
        	Spark.path("/sap/:destname/rfc", new RFCCallerAPI());
        	
        	Spark.path("/sap/:destname/inventory", () -> {
        		InventoryAPI iasAPI = new InventoryAPI();
        		MaterialAPI matAPI = new MaterialAPI();
        		
        		Spark.get("/readMaterialList", matAPI::readMaterialList, new JSONTransformer());
        		Spark.get("/readMaterialDetails", matAPI::readMaterialDetails, new JSONTransformer());
        		Spark.get("/readInventoryDocuments", iasAPI::readInventoryDocumentsFromSAP, new JSONTransformer());
        		Spark.post("/postInventoryCount", iasAPI::postInventoryCount, new JSONTransformer());
        	});
        		
        	
        	
        });
        
        
        
        
       
    }


   
}
 