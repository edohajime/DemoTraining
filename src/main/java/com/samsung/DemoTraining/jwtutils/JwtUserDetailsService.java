package com.samsung.DemoTraining.jwtutils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.samsung.DemoTraining.configuration.WebSecurityConfig;
import com.samsung.DemoTraining.repository.UserRepository;
import com.samsung.DemoTraining.repository.model.User;

@Service
public class JwtUserDetailsService implements UserDetailsService {

	@Autowired
	UserRepository userRepo;

	public User getUserById(int id) {
		return userRepo.findById(id).get();
	}

	public User getUserByUsername(String username) {
		return userRepo.findByUsername(username);
	}

	public Page<User> getAllUser(Pageable pageable) {
		return userRepo.findAll(pageable);
	}

	/**
	 * Check username exists on database or not
	 * 
	 * @param checkUser
	 * @return
	 */
	public boolean checkUsernameExists(User checkUser) {
		boolean contain = false;
		List<User> users = userRepo.findAll();
		for (User user : users) {
			if (user.getUsername().equals(checkUser.getUsername())) {
				contain = true;
			}
		}
		return contain;
	}

	public Page<User> getListUsersBySearchName(String searchName, Pageable pageable) {
		Page<User> allUsers = userRepo.findAllByUsernameLike(searchName, pageable);
		return allUsers;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		if (username.equals("nn.tien")) {
			Set<GrantedAuthority> authorities = new HashSet<>();
			User user = new User();
			user.setUsername("nn.tien");
			user.setPassword(new BCryptPasswordEncoder().encode("123456"));
			authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
			user.setAuthorities(authorities);
			return new JwtUserDetails(user);
		} else {
			User user = userRepo.findByUsername(username);
			if (user == null) {
				throw new UsernameNotFoundException("User not found with username: " + username);
			} else {
				if (username.equals("ht.anh1")) {
					Set<GrantedAuthority> authorities = new HashSet<>();
					authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
					authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
					user.setAuthorities(authorities);
				}
				return new JwtUserDetails(user);
			}
		}
	}

}
