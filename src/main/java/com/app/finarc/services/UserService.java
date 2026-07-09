package com.app.finarc.services;


import com.app.finarc.dtos.CreateUserRequest;
import com.app.finarc.dtos.LoginUserRequest;
import com.app.finarc.models.User;
import com.app.finarc.repositories.UserRepository;
import org.springframework.stereotype.Service;

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

}
