package com.samsung.DemoTraining.jwtutils.models;

import java.io.Serializable;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;

public class JwtResponseModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String token;
	private final String auth;
	private final Set<GrantedAuthority> authorities;

	public JwtResponseModel(String token, String auth, Set<GrantedAuthority> authorities) {
		super();
		this.token = token;
		this.auth = auth;
		this.authorities = authorities;
	}

	public String getToken() {
		return token;
	}

	public String getAuth() {
		return auth;
	}

	public Set<GrantedAuthority> getAuthorities() {
		return authorities;
	}
	
	

}
