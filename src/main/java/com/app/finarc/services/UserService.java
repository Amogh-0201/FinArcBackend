package com.app.finarc.services;


import com.app.finarc.dtos.user.CreateUserRequest;
import com.app.finarc.dtos.user.LoginUserRequest;
import com.app.finarc.dtos.user.UpdateUserRequest;
import com.app.finarc.models.User;
import com.app.finarc.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(CreateUserRequest req) {

        if(userRepository.findByUsername(req.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("User with username " + req.getUsername() + " already exists");
        }

        if(userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with email " + req.getEmail() + " already exists");
        }

        var newUser = new User();

        newUser.setUsername(req.getUsername());
        newUser.setEmail(req.getEmail());
        newUser.setPassword(req.getPassword());  //TODO: hash the password
        if(req.getMonthlyBudgetThreshold() != null) {
            newUser.setMonthlyBudgetThreshold(req.getMonthlyBudgetThreshold());
        }
        if(req.getCurrency() != null) {
            newUser.setCurrency(req.getCurrency());
        }

        return userRepository.save(newUser);
    }


    public User loginUser(LoginUserRequest req) throws InvalidCredentialsException {

        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow( () -> new InvalidCredentialsException("Invalid Credentials"));

        boolean passwordMatches = user.getPassword().equals(req.getPassword());  //TODO: hashed password check logic
        if(!passwordMatches) {
            throw new InvalidCredentialsException("Invalid Credentials!");
        }
        return user;
    }


    public User updateUser(UpdateUserRequest req, String userId) throws UserNotFoundException, ValidationException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + userId + " does not exist"));

        if(req.getUsername() != null && req.getUsername().length() < 3) {
            throw new ValidationException("Username must be at least 3 characters long");
        }

        if(req.getUsername() != null) {
            Optional<User> existedUser = userRepository.findByUsername(req.getUsername());
            if(existedUser.isPresent() && !existedUser.get().getId().equals(user.getId())) {
                throw new  UserAlreadyExistsException("User with username: " + req.getUsername() + " already exists");
            }
            user.setUsername(req.getUsername());
        }
        if(req.getMonthlyBudgetThreshold() != null) {
            user.setMonthlyBudgetThreshold(req.getMonthlyBudgetThreshold());
        }
        if(req.getCurrency() != null) {
            user.setCurrency(req.getCurrency());
        }

        return userRepository.save(user);
    }


    public User getUser(String userId) throws UserNotFoundException {

        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + userId + " does not exist"));

    }


    public void deleteUser(String userId) throws UserNotFoundException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + userId + " does not exist"));

        userRepository.delete(user);
    }


    public static final class UserAlreadyExistsException extends IllegalArgumentException {
        public UserAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static final class InvalidCredentialsException extends IllegalAccessException {
        public InvalidCredentialsException(String message) {
            super(message);
        }
    }

    public static final class UserNotFoundException extends IllegalArgumentException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public static final class ValidationException extends IllegalArgumentException {
        public ValidationException(String message) {
            super(message);
        }
    }

}
