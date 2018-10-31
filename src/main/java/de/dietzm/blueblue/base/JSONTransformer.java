package de.dietzm.blueblue.base;

import com.google.gson.Gson;

import spark.ResponseTransformer;

public class JSONTransformer implements ResponseTransformer{

	  private Gson gson = new Gson();

	    @Override
	    public String render(Object model) {
	        return gson.toJson(model);
	    }

}
