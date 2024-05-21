package com.juls.accesskeymanager.services;

<<<<<<< HEAD
import java.util.Calendar;
import java.util.List;
import java.util.Optional;


import org.springframework.security.crypto.password.PasswordEncoder;
=======
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
>>>>>>> parent of 66409c8 (I implemented the authentication method, specifically the user registration and also modified the user table to include the is_enabled field and also created a table for the verificaiton token et all)
import org.springframework.stereotype.Service;

import com.juls.accesskeymanager.data.models.Users;
import com.juls.accesskeymanager.data.repository.UserRepository;
<<<<<<< HEAD
import com.juls.accesskeymanager.data.repository.VerificationTokenRepository;
import com.juls.accesskeymanager.data.token.VerificationToken;
import com.juls.accesskeymanager.exceptions.UserAlreadyExistsException;

import lombok.RequiredArgsConstructor;
=======
>>>>>>> parent of 66409c8 (I implemented the authentication method, specifically the user registration and also modified the user table to include the is_enabled field and also created a table for the verificaiton token et all)

@Service
public class UserServiceImpl implements UserService{
    
<<<<<<< HEAD
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;
=======
    @Autowired
    private UserRepository userRepository;
>>>>>>> parent of 66409c8 (I implemented the authentication method, specifically the user registration and also modified the user table to include the is_enabled field and also created a table for the verificaiton token et all)


    @Override
    public String getUserEmailById(Long id){
        Optional <Users> user = this.userRepository.findById(id);
        return user.get().getEmail();
    }

    @Override
    public Long getUserIdByEmail(String email){
        Optional <Users> user = this.userRepository.findByEmail(email);
        return user.get().getUserId();
    }
    
<<<<<<< HEAD
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
        VerificationToken verificationToken = this.verificationTokenRepository.findByToken(token);
        if (verificationToken==null){
            return "Invalid Verification Token";
        }

        var user = verificationToken.getUser();
        Calendar calendar = Calendar.getInstance();
        if((verificationToken.getExpirationTime().getTime() - calendar.getTime().getTime()<= 0)){
            this.verificationTokenRepository.delete(verificationToken);
            return "Verification Token Expired";
        }
        user.setEnabled(true);
        this.userRepository.save(user);
        this.verificationTokenRepository.delete(verificationToken);
        return "valid";
    }

    public void saveVerificationToken(Users user, String token){
        var verificationToken = new VerificationToken(token, user);
        this.verificationTokenRepository.save(verificationToken);
    }

    public VerificationToken findToken(String token){
        VerificationToken theToken = this.verificationTokenRepository.findByToken(token);
        return theToken;
    }
    
=======
>>>>>>> parent of 66409c8 (I implemented the authentication method, specifically the user registration and also modified the user table to include the is_enabled field and also created a table for the verificaiton token et all)
}
