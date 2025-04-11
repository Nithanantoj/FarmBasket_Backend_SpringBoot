package com.ecommerce.farm_basket.service;

import com.ecommerce.farm_basket.entity.User;
import com.ecommerce.farm_basket.excetion.UserAlreadyExistsException;
import com.ecommerce.farm_basket.excetion.UserNotFoundException;
import com.ecommerce.farm_basket.repository.UserRepository;
import com.ecommerce.farm_basket.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil; // Inject JwtUtil


    private final BCryptPasswordEncoder PasswordEncoder = new BCryptPasswordEncoder();


    public String registerUser(User user) {

        if(userRepository.findByEmail(user.getEmail()) != null){
            throw new UserAlreadyExistsException("User already registered with email: " + user.getEmail());
        }

        user.setPassword(PasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "User register Successfully";
    }

    public String loginUser(User user) {
        User existuser = userRepository.findByEmail(user.getEmail());

        if (existuser == null) {
            throw new UserNotFoundException("User with email " + user.getEmail() + " not found.");
        }

        if (!PasswordEncoder.matches(user.getPassword(), existuser.getPassword())) {
            throw new UserNotFoundException("Incorrect password.");
        }

        String token = jwtUtil.generateToken(
                existuser.getId().toString(),
                existuser.getEmail(),
                existuser.getRole()
        );

        return "Login Successful and your Token is: " + token;
    }
}
