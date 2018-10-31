package de.dietzm.blueblue.base;

import spark.RouteGroup;
import spark.Spark;

public class HealthCheckAPI implements RouteGroup{

	@Override
	public void addRoutes() {
		Spark.put("/liveness", (q,r)->{
			return "healthy";
		});
		Spark.post("/liveness", (q,r)->{
			return "healthy";
		});
		Spark.get("/liveness", (q,r)->{
			return "healthy";
		});
		Spark.options("/liveness", (q,r)->{
			return "healthy";
		});
		Spark.head("/liveness", (q,r)->{
			return "";
		});
	}

}
