package de.dietzm.blueblue.model;

import com.google.gson.annotations.SerializedName;

public class Material {

	@SerializedName(value="materialnumber", alternate= {"MATERIAL"} )
	private String matnumber;
	
	@SerializedName(value="description", alternate = {"MATL_DESC"})
	private String description;
	
	public String getMatnumber() {
		return matnumber;
	}
	public void setMatnumber(String matnumber) {
		this.matnumber = matnumber;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
