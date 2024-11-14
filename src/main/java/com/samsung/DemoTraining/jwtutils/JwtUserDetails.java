package com.samsung.DemoTraining.jwtutils;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.samsung.DemoTraining.repository.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtUserDetails implements UserDetails {
	User user;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (user.getAuthorities() == null || user.getAuthorities().isEmpty()) {
			return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
		}
		
		return user.getAuthorities();
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}
	
}
