package com.juls.accesskeymanager.services;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.juls.accesskeymanager.data.models.AuthenticationRequest;
import com.juls.accesskeymanager.data.models.Users;
import com.juls.accesskeymanager.data.repository.UserRepository;
import com.juls.accesskeymanager.data.repository.VerificationTokenRepository;
import com.juls.accesskeymanager.data.token.VerificationToken;
import com.juls.accesskeymanager.exceptions.UserAlreadyExistsException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    
    @Autowired
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationRepository;

    

    @Override
    public Users getUserByEmail(String email){
        return this.userRepository.findByEmail(email).get();
    }

    public List <Users> getUsers() {
        return this.userRepository.findAll();
    }

    public Users registerUser(AuthenticationRequest authenticationRequest) {
        Optional <Users> user = this.userRepository.findByEmail(authenticationRequest.email());
        if (user.isPresent()){
            throw new UserAlreadyExistsException("User with email "+authenticationRequest.email()+" already exist");
        }
        var newUser = new Users();
        newUser.setEmail(authenticationRequest.email());
        newUser.setPassword(passwordEncoder.encode(authenticationRequest.password()));
        newUser.setRole(authenticationRequest.role());
        return this.userRepository.save(newUser);
    }


    @Override
    public String validateToken(String token) {
        VerificationToken verificationToken = this.verificationRepository.findByToken(token);
        if (verificationToken==null){
            return "Invalid Verification Token";
        }

        var user = verificationToken.getUser();
        Calendar calendar = Calendar.getInstance();
        if((verificationToken.getExpirationTime().getTime() - calendar.getTime().getTime()<= 0)){
            this.verificationRepository.delete(verificationToken);
            return "Verification Token Expired";
        }
        user.setEnabled(true);
        this.userRepository.save(user);
        return "valid";
    }


    public void saveVerificationToken(Users user, String token){
        var verificationToken = new VerificationToken(token, user);
        this.verificationRepository.save(verificationToken);
    }

    public VerificationToken findToken(String token){
        VerificationToken theToken = this.verificationRepository.findByToken(token);
        return theToken;
    }
    
}
