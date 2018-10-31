package de.dietzm.blueblue.base;

import com.google.gson.Gson;

import spark.Spark;

public class APIResultBuilder {

	public static APIResult success() {
		APIResult result = new APIResult(APIResult.STATUS_SUCCESS);
		return result;
	}
	
	public static APIResult success(Object resultObject) {
		APIResult result = new APIResult(APIResult.STATUS_SUCCESS);
		result.setData(resultObject);
		return result;
	}

	public static APIResult successMessage(String message) {
		APIResult result = new APIResult(APIResult.STATUS_SUCCESS);
		result.setMessage(message);
		return result;
	}

	public static APIResult errorAndHalt(int httpCode, String message) {
		APIResult result = new APIResult(APIResult.STATUS_ERROR);
		result.setMessage(message);
		String resultJSON = new Gson().toJson(result);
		Spark.halt(httpCode, resultJSON);
		return result;
	}
	
}
