package de.dietzm.blueblue.auth;

import java.util.HashMap;

import spark.Request;

public class AuthenticationManager {

	private HashMap<String, String[]> authorized = new HashMap<>();

	public AuthenticationManager() {
		authorized.put("Bearer IwOGYMnlT6z2YxOTQ5MGE3YmMdmdFkNTVk", new String[] { "api_access" });
		authorized.put("Bearer IwOG122lT6z2YxOTQ5MGE3YmMdmdFkNTVk", new String[] { "api_access", "admin" });
	}

	public boolean isAuthorized(Request request) {
		String authKey = request.headers("Authorization");
		if(authorized.containsKey(authKey))
			return true;
		return false;
	}

	public boolean isAuthorizedFor(Request request, String scope) {
		String authKey = request.headers("Authorization");
		if(authorized.containsKey(authKey)) {
			String[] scopes = authorized.get(authKey);
			for (int i = 0; i < scopes.length; i++) {
				if(scopes[i].equalsIgnoreCase(scope)) {
					return true;
				}
			}
		}
		return false;
	}

}
