package com.alkemy.ong.util.dataseed;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.alkemy.ong.model.Role;
import com.alkemy.ong.model.User;
import com.alkemy.ong.repository.RoleRepository;
import com.alkemy.ong.repository.UserRepository;
import com.alkemy.ong.util.ERole;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDataSeed {

	@Value("${dataseed.user.number-of-users}")
	private int numberOfUsers;
	
	@Value("${dataseed.user.admin-name}")
	private String admin;
	
	@Value("${dataseed.user.regular-user-name}")
	private String user;
	
	@Value("${dataseed.user.password}")
	private String password;
	
	@Value("${dataseed.user.photo}")
	private String photo;
	
	private final RoleRepository roleRepository;
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	
	public void loadUserData() {
		if (userDataIsEmpty()) {
			makeAdminUsers();
			makeRegularUsers();
		} else
			log.info("Users data is not empty");
	}
	
	private boolean userDataIsEmpty() {
		return userRepository.count() == 0;
	}
	
	private void makeAdminUsers() {
		Role role = getRole(ERole.ROLE_ADMIN);
		saveUsers(admin, password, role);
	}
	
	private void makeRegularUsers() {
		Role role = getRole(ERole.ROLE_USER);
		saveUsers(user, password, role);
	}
	
	private Role getRole(ERole roleEnum) {
		Role role = roleRepository.findByName(roleEnum.name());
		if (role == null) {
			role = new Role();
			role.setName(roleEnum.name());
			role = roleRepository.save(role);
		}
		return role;
	}
	
	private void saveUsers(String name, String password, Role role) {
		for (int i = 1; i <= numberOfUsers; i++) {
			saveUser(name + formatNumber(i), password, role);
		}
	}
		
	private void saveUser(String name, String password, Role role) {
		User user = new User();
		user.setFirstName(name);
		user.setLastName(name);
		user.setEmail(name + "@email.com");
		user.setPhoto(photo);
		user.setPassword(passwordEncoder.encode(password));
		user.setRoleId(role);
		user = userRepository.save(user);
		log.info("Added {} user: {}", role.getName().replaceAll("ROLE_", "").toLowerCase(), user);
	}
	
	private String formatNumber(int n) {
		return n < 10 ? "0" + n : String.valueOf(n);
	}
	
}
