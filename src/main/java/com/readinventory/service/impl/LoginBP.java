package com.readinventory.service.impl;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.readinventory.model.Login;
import com.readinventory.model.Users;
import com.readinventory.repository.UserRepository;

@Service
public class LoginBP implements UserDetailsService{


	@Autowired
	private PasswordEncoder bcryptEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Users user=userRepository.findByUsername(username);
		
		System.out.println("user    "+user.getUsername());
		
		if (user == null) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
		else
		{
		return new User(user.getUsername(), user.getPassword(),
				new ArrayList<>());	
		}
	}
	
	
	public Users save(Login user) {
		Users newUser = new Users();
		newUser.setUsername(user.getUsername());
		System.out.println(user.getUsername()+"                               "+user.getPassword());
		newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
		return userRepository.save(newUser);
	}
	
	
}
