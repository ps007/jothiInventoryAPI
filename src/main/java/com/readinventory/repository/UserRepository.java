package com.readinventory.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


import com.readinventory.model.Users;

@Repository
public interface UserRepository extends CrudRepository<Users,Integer>{

	Users findByUsername(String username);
}
