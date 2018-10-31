package de.dietzm.blueblue.base;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class APIResult {
	
	public static final int STATUS_SUCCESS = 1;
	public static final int STATUS_WARNING = 2;
	public static final int STATUS_ERROR = 3;
	public static final int STATUS_FATAL = 4;
	
	private boolean success = false;
	private String status;
	private String message;
	private String[] additionalMessages;
	
	private Object data;
	
	public APIResult(int statusCode) {
		if(statusCode == STATUS_SUCCESS) {
			this.status = "SUCCESS";
			success = true;
		}
		
		if(statusCode == STATUS_WARNING)
			this.status = "WARNING";
		
		if(statusCode == STATUS_ERROR)
			this.status = "ERROR";
		
		if(statusCode == STATUS_FATAL)
			this.status = "FATAL";
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String[] getAdditionalMessages() {
		return additionalMessages;
	}

	public void setAdditionalMessages(String[] additionalMessages) {
		this.additionalMessages = additionalMessages;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	
	public APIResult addData(String name, JsonElement jsonElement) {
		JsonObject localData;
		if(data == null) {
			localData = new JsonObject();
		} else if (!(data instanceof JsonObject)) {
			Object currentData = data;
			localData = new JsonObject();
			localData.addProperty("__DATA__", new Gson().toJson(currentData));
		} else {
			localData = (JsonObject) data;
		}
		
		localData.add(name, jsonElement);
		
		
		data = localData;
		
		return this;
	}

	
}
