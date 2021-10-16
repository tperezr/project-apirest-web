package com.alkemy.ong.controller;

import com.alkemy.ong.dto.UserRequest;
import com.alkemy.ong.model.User;
import com.alkemy.ong.repository.UserRepository;
import com.alkemy.ong.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

import com.alkemy.ong.dto.UserDTO;
import com.alkemy.ong.service.impl.UserServiceImpl;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Tag(name = "Users")
@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {

    @Autowired
    private IUserService iUserService;
    UserServiceImpl userService;

    @GetMapping()

    public List<UserDTO> getUserList() {
        return userService.findAllUsers();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable long id){

        try {
            return ResponseEntity.status(HttpStatus.OK).body(userService.softDeleteUser(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> updateUser(@PathVariable ("id") Long id, @ModelAttribute UserRequest user){
        try{
            return new ResponseEntity<>(iUserService.updateUser(id, user), HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
