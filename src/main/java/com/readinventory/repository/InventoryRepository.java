package com.readinventory.repository;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.readinventory.model.Inventory;

@Repository
public interface InventoryRepository extends CrudRepository<Inventory,Integer>{

	List<Inventory> findByIsbn(String isbn);
	
}
