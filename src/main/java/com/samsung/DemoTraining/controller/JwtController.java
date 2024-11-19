package com.samsung.DemoTraining.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.samsung.DemoTraining.configuration.WebSecurityConfig;
import com.samsung.DemoTraining.jwtutils.JwtUserDetailsService;
import com.samsung.DemoTraining.jwtutils.TokenManager;
import com.samsung.DemoTraining.jwtutils.models.JwtRequestModel;
import com.samsung.DemoTraining.jwtutils.models.JwtResponseModel;
import com.samsung.DemoTraining.jwtutils.models.MessageResponseModel;
import com.samsung.DemoTraining.repository.UserRepository;
import com.samsung.DemoTraining.repository.model.User;
import com.samsung.DemoTraining.utilities.Utilities;

@CrossOrigin
@RestController
public class JwtController {
	private static final int PAGE_SIZE = 6;

	@Autowired
	private WebSecurityConfig config;

	@Autowired
	private JwtUserDetailsService userDetailsService;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private TokenManager tokenManager;

	@PostMapping("/login")
	public ResponseEntity<MessageResponseModel> createToken(@RequestBody JwtRequestModel request) throws Exception {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
		} catch (DisabledException e) {
			return ResponseEntity.ok(new MessageResponseModel(false, e.getMessage()));
		} catch (BadCredentialsException e) {
			return ResponseEntity.ok(new MessageResponseModel(false, e.getMessage()));
		}

		final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
		final String jwtToken = tokenManager.generateJwtToken(userDetails);
		final Set<GrantedAuthority> authorities = new HashSet<>();
		authorities.addAll(userDetails.getAuthorities());
		return ResponseEntity
				.ok(new JwtResponseModel(jwtToken, userDetails.getUsername(), authorities, true, "Login success!"));
	}

	@PostMapping(value = "/register")
	public ResponseEntity<MessageResponseModel> register(User user) {
		MessageResponseModel msgRes = new MessageResponseModel();
		if (!Utilities.validateUsernamePassword(user, msgRes)) {
			return ResponseEntity.ok(msgRes);
		}

		if (userDetailsService.checkUsernameExists(user)) {
			msgRes.setMessage("Username existed");
			msgRes.setStatus(false);
			return ResponseEntity.ok(msgRes);
		}

		User userAdd = User.builder().username(user.getUsername())
				.password(config.passwordEncoder().encode(user.getPassword())).build();
		Set<GrantedAuthority> authorities = new HashSet<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		userAdd.setAuthorities(authorities);

		userRepo.save(userAdd);

		return ResponseEntity.ok(new MessageResponseModel(true, "Register user successfully!"));
	}

	@PostMapping(value = "/modify-user")
	public ResponseEntity<MessageResponseModel> modifyUser(User user) {
		MessageResponseModel msgRes = new MessageResponseModel();
		if (!Utilities.validateUsername(user, msgRes)) {
			return ResponseEntity.ok(msgRes);
		}

		// cần lấy ID vì username có thể thay đổi
		User userUpdate = userDetailsService.getUserById(user.getId());

		// nếu username là mới thì cập nhật username
		if (!userDetailsService.checkUsernameExists(user)) {
			userUpdate.setUsername(user.getUsername());
		}

		userRepo.save(userUpdate);

		return ResponseEntity.ok(new MessageResponseModel(true, "Modify user successfully!"));
	}

	@GetMapping(value = "/del-user")
	public ResponseEntity<MessageResponseModel> deleteUser(@RequestParam(name = "username") String username) {
		User userDel = userDetailsService.getUserByUsername(username);

		userRepo.delete(userDel);

		return ResponseEntity.ok(new MessageResponseModel(true, "Delete successful"));
	}

	@GetMapping("/user")
	public ResponseEntity<User> getUser(@RequestParam(name = "username") String username) {
		if (username.equals("nn.tien")) {
			User user = new User();
			user.setUsername("nn.tien");
			user.setFullname("Nguyen Ngoc Tien");
			user.setPassword("123456");
			user.setBirthyear((short) 1980);
			user.setEmail("nn.tien@samsung.com");
			user.setPhone("0123456789");
			return ResponseEntity.ok(user);
		}

		User user = userDetailsService.getUserByUsername(username);
		if (user.getUsername().equals("ht.anh1")) {
			Set<GrantedAuthority> authorities = new HashSet<>();
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
			authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
			user.setAuthorities(authorities);
		}
		return ResponseEntity.ok(user);
	}

	@PostMapping(value = "/update-profile")
	public ResponseEntity<MessageResponseModel> updateProfile(User user) {
		MessageResponseModel msgRes = new MessageResponseModel();
		if (!Utilities.validateUserProfiles(user, msgRes)) {
			return ResponseEntity.ok(msgRes);
		}

		User userUpdate = userDetailsService.getUserByUsername(user.getUsername());

		userUpdate.setFullname(user.getFullname());
		userUpdate.setBirthyear(user.getBirthyear());
		userUpdate.setEmail(user.getEmail());
		userUpdate.setPhone(user.getPhone());
		userUpdate.setBio(user.getBio());

		userRepo.save(userUpdate);

		msgRes.setStatus(true);
		msgRes.setMessage("Update profile successfully!");
		return ResponseEntity.ok(msgRes);
	}

	@PostMapping(value = "/change-pwd")
	public ResponseEntity<MessageResponseModel> changePwd(User user, Principal principal) {
		MessageResponseModel msgRes = new MessageResponseModel();
		if (!Utilities.validateUsernamePassword(user, msgRes)) {
			return ResponseEntity.ok(msgRes);
		}

		User userUpdate = userDetailsService.getUserByUsername(user.getUsername());

		// username ko ton tai thi sua username password, neu ton tai chi sua password
		if (!userDetailsService.checkUsernameExists(user)) {
			userUpdate.setUsername(user.getUsername());
			userUpdate.setPassword(config.passwordEncoder().encode(user.getPassword()));
		} else {
			userUpdate.setPassword(config.passwordEncoder().encode(user.getPassword()));
		}

		userRepo.save(userUpdate);

		msgRes.setStatus(true);
		msgRes.setMessage("Modify successful");
		return ResponseEntity.ok(msgRes);
	}

	@GetMapping("/all-users")
	public ResponseEntity<Page<User>> getAllUsers(@RequestParam(name = "page") Integer page,
			@RequestParam(name = "searchName") String searchName) {
		Pageable pageable = PageRequest.of(page, PAGE_SIZE);
		Page<User> users = null;
		if (searchName != null && searchName != "" && searchName != "*") {
			users = userDetailsService.getListUsersBySearchName(searchName, pageable);
		} else {
			users = userDetailsService.getAllUser(pageable);
		}

		return ResponseEntity.ok(users);
	}

}
