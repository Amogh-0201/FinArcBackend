package com.app.finarc.controllers;


import com.app.finarc.dtos.common.ExceptionDto;
import com.app.finarc.dtos.user.*;
import com.app.finarc.security.JwtProvider;
import com.app.finarc.services.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;
    private final JwtProvider jwtProvider;

    public UserController(UserService userService, ModelMapper modelMapper, JwtProvider jwtProvider) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("")
    public ResponseEntity<AuthUserResponse> signUpUser( @RequestBody @Valid CreateUserRequest req) {

        var userEntity = userService.createUser(req);
        var token = jwtProvider.generateToken(userEntity.getId(), userEntity.getUsername());

        AuthUserResponse initialResponse = modelMapper.map(userEntity, AuthUserResponse.class);

        AuthUserResponse response = initialResponse.toBuilder()
                .token(token)
                .build();

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{username}")
                .buildAndExpand(response.getUsername())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthUserResponse> signInUser(@RequestBody @Valid LoginUserRequest req) throws UserService.InvalidCredentialsException {

        var userEntity = userService.loginUser(req);

        var token = jwtProvider.generateToken(userEntity.getId(), userEntity.getUsername());

        AuthUserResponse initialResponse = modelMapper.map(userEntity, AuthUserResponse.class);

        AuthUserResponse response = initialResponse.toBuilder()
                .token(token)
                .build();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("")
    public ResponseEntity<UserResponse> updateUser (
            @AuthenticationPrincipal String userId,
            @RequestBody UpdateUserRequest req
    ) throws UserService.UserNotFoundException, UserService.ValidationException {

        var userEntity = userService.updateUser(req, userId);

        UserResponse userResponse = modelMapper.map(userEntity, UserResponse.class);

        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("")
    public ResponseEntity<UserResponse> getUser (@AuthenticationPrincipal String userId) throws UserService.UserNotFoundException {

        var userEntity = userService.getUser(userId);

        UserResponse userResponse = modelMapper.map(userEntity, UserResponse.class);

        return ResponseEntity.ok(userResponse);
    }

    @DeleteMapping("")
    public ResponseEntity<DeleteUserResponse> deleteUser(@AuthenticationPrincipal String userId) throws UserService.UserNotFoundException {

        userService.deleteUser(userId);
        DeleteUserResponse deleteUserResponse = new DeleteUserResponse();
        deleteUserResponse.setMessage("Successfully deleted the user");
        return ResponseEntity.ok(deleteUserResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionDto> handleException(Exception e) {
        String message;
        HttpStatus statusCode;

        if( e instanceof UserService.UserNotFoundException ||
                e instanceof UserService.UserAlreadyExistsException ||
                e instanceof UserService.ValidationException
        ) {

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

            message = e.getMessage();
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        ExceptionDto exceptionDto = new ExceptionDto();
        exceptionDto.setMessage(message);

        return ResponseEntity.status(statusCode).body(exceptionDto);
    }

}
