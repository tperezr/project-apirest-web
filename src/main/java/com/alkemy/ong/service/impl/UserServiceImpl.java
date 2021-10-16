package com.alkemy.ong.service.impl;

import com.alkemy.ong.dto.UserRequest;
import com.alkemy.ong.model.Role;
import com.alkemy.ong.dto.UserDTO;
import com.alkemy.ong.model.User;
import com.alkemy.ong.repository.RoleRepository;
import com.alkemy.ong.repository.UserRepository;
import com.alkemy.ong.util.ERole;
import org.springframework.beans.factory.annotation.Autowired;
import com.alkemy.ong.service.IUserService;
import com.alkemy.ong.service.MailContentBuilder;
import com.alkemy.ong.service.SendGridService;
import com.sendgrid.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserDetailsService, IUserService {

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    private AmazonClient amazonClient;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private final MessageSource messageSource;
    private final SendGridService sendGridService;
    private final MailContentBuilder mailContentBuilder;

    public String signUpUser(User user) {
        try {
            String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());

            user.setPassword(encodedPassword);

            Role role = new Role();
            if (roleRepository.findByName(ERole.ROLE_USER.name()) == null) {
                role.setDescription(ERole.ROLE_USER.name());
                role.setName(ERole.ROLE_USER.name());
                roleRepository.save(role);
            } else {
                role = roleRepository.findByName(ERole.ROLE_USER.name());
            }
            role.setCreatedAt(Timestamp.from(Instant.now()));
            user.setRoleId(role);
            user.setCreatedAt(LocalDateTime.now());

            userRepository.save(user);

            String userCreatedMessage = messageSource.getMessage("user.created", new Object[]{"User"}, Locale.US);

            Response emailSendResponse = sendGridService.sendEmail(user, mailContentBuilder.buildWelcomeEmail(user));
//            log.info(messageSource.getMessage("sendgrid.status.code",
//                    new Object[]{emailSendResponse.getStatusCode(), emailSendResponse.getBody()},
//                    Locale.US));
            return userCreatedMessage;
        } catch (Exception e) {
        	e.printStackTrace();
            String registerError = messageSource.getMessage("register.error", null, Locale.US);
            return registerError;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        String userNotFoundMsg = messageSource.getMessage("user.not.found", new Object[]{"User"}, Locale.US);
        Optional<User> user = userRepository.findByEmail(email);
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(user.get().getRoleId().getName()));
        try {
            return new org.springframework.security.core.userdetails.User(user.get().getEmail(), user.get().getPassword(), authorities);
        } catch (Exception e) {
            throw new UsernameNotFoundException(userNotFoundMsg);
        }
    }

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }


    @Override
    public List<UserDTO> findAllUsers() {
        List<User> listUser = userRepository.findAll();
        List<UserDTO> listUserDTO = new ArrayList<>();

        listUserDTO = listUser.stream().map(new Function<User, UserDTO>() {
                                                @Override
                                                public UserDTO apply(User u) {
                                                    return new UserDTO(
                                                            u.getFirstName(),
                                                            u.getLastName(),
                                                            u.getEmail(),
                                                            u.getPhoto(),
                                                            u.getRoleId()
                                                    );
                                                }
                                            }
        ).collect(Collectors.toList());


        return listUserDTO;
    }

    @Override
    public String softDeleteUser(long id) throws UsernameNotFoundException {
        try {
            userRepository.deleteById(id);
            String userDeleteMessage = messageSource.getMessage("user.deleted", new Object[]{"User"}, Locale.US);
            return userDeleteMessage;
        } catch (Exception e) {
            String userNotFoundByIdMsg = messageSource.getMessage("user.not.found.by.id", new Object[]{"User"}, Locale.US);
            throw new UsernameNotFoundException(userNotFoundByIdMsg);
        }
    }

    @Override
    public User updateUser(Long id, UserRequest user) {
        Optional<User> userToUp = userRepository.findById(id);
        User usr = userToUp.get();
        if (id == userToUp.get().getId()) {
            if (userToUp.isPresent()) {
                String firstName = user.getFirstName() == null ? usr.getFirstName() : user.getFirstName();
                String lastName = user.getLastName() == null ? usr.getLastName() : user.getLastName();

                if (user.getPhoto() != null && usr.getPhoto() != null) {
                    amazonClient.deleteFileFromS3Bucket(usr.getPhoto());
                    String urlImage = amazonClient.uploadFile(user.getPhoto());
                    usr.setPhoto(urlImage);
                }

                String email = user.getEmail() == null ? usr.getEmail() : user.getEmail();
                String password = user.getPassword() == null ? usr.getPassword() : user.getPassword();
                usr.setFirstName(firstName);
                usr.setLastName(lastName);
                usr.setEmail(email);
                usr.setPassword(password);
            }
            return userRepository.save(usr);
        } else {
            String userNotFoundMsg = messageSource.getMessage("user.not.found", new Object[]{"User"}, Locale.US);
            throw new RuntimeException(userNotFoundMsg);
        }

    }
}
