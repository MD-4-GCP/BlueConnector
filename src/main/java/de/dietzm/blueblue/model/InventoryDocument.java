package de.dietzm.blueblue.model;

public class InventoryDocument {

	private String SAPDocumentId;
	private int plant;
	private String warehouse;
	private String location;
	private String materialNumber;
	private String materialDescription;
	private String EANCode;
	private String UoM;
	private int item;
	private boolean counted; 
	private double count;
	private double inStock;
	private int SAPFiscalYear;
	
	public int getPlant() {
		return plant;
	}
	public void setPlant(int plant) {
		this.plant = plant;
	}
	public String getWarehouse() {
		return warehouse;
	}
	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getMaterialNumber() {
		return materialNumber;
	}
	public void setMaterialNumber(String materialNumber) {
		this.materialNumber = materialNumber;
	}
	public String getMaterialDescription() {
		return materialDescription;
	}
	public void setMaterialDescription(String materialDescription) {
		this.materialDescription = materialDescription;
	}
	public String getEANCode() {
		return EANCode;
	}
	public void setEANCode(String eANCode) {
		EANCode = eANCode;
	}
	public String getUoM() {
		return UoM;
	}
	public void setUoM(String uoM) {
		UoM = uoM;
	}
	public boolean isCounted() {
		return counted;
	}
	public void setCounted(boolean counted) {
		this.counted = counted;
	}

	
	public double getCount() {
		return count;
	}
	public void setCount(double count) {
		this.count = count;
	}
	public double getInStock() {
		return inStock;
	}
	public void setInStock(double inStock) {
		this.inStock = inStock;
	}
	public String getSAPDocumentId() {
		return SAPDocumentId;
	}
	public void setSAPDocumentId(String sAPDocumentId) {
		SAPDocumentId = sAPDocumentId;
	}
	public int getSAPFiscalYear() {
		return SAPFiscalYear;
	}
	public void setSAPFiscalYear(int sAPFiscalYear) {
		SAPFiscalYear = sAPFiscalYear;
	}
	public int getItem() {
		return item;
	}
	public void setItem(int item) {
		this.item = item;
	}
	
	
}
