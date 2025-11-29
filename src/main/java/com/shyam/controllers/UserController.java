package com.shyam.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shyam.entities.UserEntity;
import com.shyam.exceptions.RequestedEntityNotFoundException;
import com.shyam.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserEntity>> getAllUserDetails() throws RequestedEntityNotFoundException {
        List<UserEntity> user = userService.getAll();

        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(user);
    }
    
    @GetMapping("/{user-id}")
    public ResponseEntity<UserEntity> getUserDetails(
        @PathVariable("user-id") int userId
    ) throws RequestedEntityNotFoundException {
        UserEntity user = userService.getUserById(userId)
                            .orElseThrow(
                                () -> new RequestedEntityNotFoundException("User not found with id : " + userId)
                            );

        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(user);
    }
}
