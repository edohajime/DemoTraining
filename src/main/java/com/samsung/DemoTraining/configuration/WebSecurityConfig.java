package com.samsung.DemoTraining.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.samsung.DemoTraining.jwtutils.JwtAuthenticationEntryPoint;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.HashSet;
import java.util.Set;

import com.samsung.DemoTraining.jwtutils.JwtFilter;
import com.samsung.DemoTraining.jwtutils.JwtUserDetails;
import com.samsung.DemoTraining.jwtutils.JwtUserDetailsService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	@Autowired
	private JwtUserDetailsService userDetailsService;
	
	@Autowired
	private JwtAuthenticationEntryPoint authenticationEntryPoint;

	@Autowired
	private JwtFilter filter;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        request -> request.requestMatchers("/login").permitAll()
                                .anyRequest().authenticated())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .build();
	}
	
	@Bean
	AuthenticationManager customAuthenticationManager() {
		return authentication -> {
			String username = authentication.getName();
			String password = authentication.getCredentials().toString();
			
			UserDetails user = userDetailsService.loadUserByUsername(username);
			if (user == null || !passwordEncoder().matches(password, user.getPassword())) {
				throw new BadCredentialsException("Invalid username or password");
			}
			
			return new UsernamePasswordAuthenticationToken(username, password, user.getAuthorities());
		};
	}
	
}
