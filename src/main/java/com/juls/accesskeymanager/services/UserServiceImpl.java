package com.juls.accesskeymanager.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.juls.accesskeymanager.data.models.AuthenticationRequest;
import com.juls.accesskeymanager.data.models.Users;
import com.juls.accesskeymanager.data.repository.UserRepository;
import com.juls.accesskeymanager.exceptions.UserAlreadyExistsException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public String getUserEmailById(Long id){
        Optional <Users> user = this.userRepository.findById(id);
        return user.get().getEmail();
    }

    public Long getUserIdByEmail(String email){
        Optional <Users> user = this.userRepository.findByEmail(email);
        return user.get().getUserId();
    }
    
    @Override
    public Users getUserByEmail(String email){
        return this.userRepository.findByEmail(email).get();
    }

    @Override
    public List<Users> getUsers() {
        return this.userRepository.findAll();
    }

    @Override
    public Users registerUser(AuthenticationRequest authenticationRequest) {
        Optional <Users> user = this.userRepository.findByEmail(authenticationRequest.getEmail());
        if (user.isPresent()){
            throw new UserAlreadyExistsException("User with email "+authenticationRequest.getEmail()+" already exist");
        }
        var newUser = new Users();
        newUser.setEmail(authenticationRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(authenticationRequest.getPassword()));
        newUser.setRole(authenticationRequest.getRole());
        return this.userRepository.save(newUser);
    }
    

    @Override
    public String validateToken(String token) {
        // TODO Auto-generated method stub
        return null;
    }
}
