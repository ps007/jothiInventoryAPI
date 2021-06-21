package com.readinventory.model;

import java.util.List;

public class AddInventoryResponse {
	
	public List<Inventory>  inventoryList;
	public String error;
	public List<Inventory> getInventoryList() {
		return inventoryList;
	}
	public void setInventoryList(List<Inventory> inventoryList) {
		this.inventoryList = inventoryList;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
	

}
