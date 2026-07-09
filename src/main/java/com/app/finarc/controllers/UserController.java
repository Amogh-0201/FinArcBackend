package com.app.finarc.controllers;


import com.app.finarc.dtos.CreateUserRequest;
import com.app.finarc.dtos.ExceptionDto;
import com.app.finarc.dtos.LoginUserRequest;
import com.app.finarc.dtos.UserResponse;
import com.app.finarc.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("")
    public ResponseEntity<UserResponse> signUpUser( @RequestBody @Valid CreateUserRequest req) {

        var userEntity = userService.createUser(req);

        UserResponse userResponse = UserResponse.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .monthlyBudgetThreshold(userEntity.getMonthlyBudgetThreshold())
                .currency(userEntity.getCurrency())
                .build();

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userResponse.getId())
                .toUri();

        return ResponseEntity.created(location).body(userResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> signInUser(@RequestBody @Valid LoginUserRequest req) throws UserService.InvalidCredentialsException {

        var userEntity = userService.loginUser(req);

        UserResponse userResponse = UserResponse.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .monthlyBudgetThreshold(userEntity.getMonthlyBudgetThreshold())
                .currency(userEntity.getCurrency())
                .build();

        return ResponseEntity.ok(userResponse);
    }


    @ExceptionHandler
    public ResponseEntity<ExceptionDto> handleException(Exception e) {
        String message;
        HttpStatus statusCode;

        if(e instanceof UserService.UserAlreadyExistsException) {

            message = e.getMessage();
            statusCode = HttpStatus.BAD_REQUEST;

        } else if(e instanceof MethodArgumentNotValidException validEx) {

            message = validEx.getBindingResult().getFieldErrors().stream()
                    .map(org.springframework.validation.FieldError::getDefaultMessage)
                    .collect(java.util.stream.Collectors.joining(", "));
            statusCode = HttpStatus.BAD_REQUEST;

        } else if (e instanceof UserService.InvalidCredentialsException) {
            message = e.getMessage();
            statusCode = HttpStatus.UNAUTHORIZED;
        } else {

            message = "Unknown Error Occurred";
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        ExceptionDto exceptionDto = new ExceptionDto();
        exceptionDto.setMessage(message);

        return ResponseEntity.status(statusCode).body(exceptionDto);
    }

}
