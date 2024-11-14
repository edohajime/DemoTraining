package com.samsung.DemoTraining.jwtutils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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

	public List<User> getAllUser() {
		return userRepo.findAll();
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

	public List<User> getListUsersBySearchName(String searchName) {
		List<User> allUsers = userRepo.findAll();
		if (searchName.equals("*")) {
			return allUsers;
		}
		return search(allUsers, searchName);
	}

	private List<User> search(List<User> allUsers, String searchName) {
		List<User> results = new ArrayList<>();
		allUsers.forEach(user -> {
			if (user.getUsername().contains(searchName)) {
				results.add(user);
			}
		});
		return results;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		if (username.equals("nn.tien")) {
			Set<GrantedAuthority> authorities = new HashSet<>();
			User user = new User();
			user.setUsername("nn.tien");
			user.setPassword("123456");
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
