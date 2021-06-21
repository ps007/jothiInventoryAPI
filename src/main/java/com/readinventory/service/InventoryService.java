package com.readinventory.service;

import java.util.List;

import com.readinventory.model.AddInventoryResponse;
import com.readinventory.model.InventoryAPICall;

public interface InventoryService {
	
	public AddInventoryResponse  fileUpload(InventoryAPICall inventoryAPICall) throws Exception;
	
	
	
}
