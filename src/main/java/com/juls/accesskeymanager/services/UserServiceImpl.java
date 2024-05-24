package com.juls.accesskeymanager.services;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.swing.text.html.Option;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.juls.accesskeymanager.data.models.AuthenticationRequest;
import com.juls.accesskeymanager.data.models.Password;
import com.juls.accesskeymanager.data.models.ResetPasswordRequest;
import com.juls.accesskeymanager.data.models.Users;
import com.juls.accesskeymanager.data.repository.UserRepository;
import com.juls.accesskeymanager.data.repository.VerificationTokenRepository;
import com.juls.accesskeymanager.data.token.VerificationToken;
import com.juls.accesskeymanager.exceptions.NotFoundException;
import com.juls.accesskeymanager.exceptions.UserAlreadyExistsException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{
    
    @Autowired
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationRepository;
    private final EmailService emailService;

    

    @Override
    public Users getUserByEmail(String email){
        return this.userRepository.findByEmail(email).get();
    }

    public List <Users> getUsers() {
        return this.userRepository.findAll();
    }

    public Users getUserByToken(String token){
        return verificationRepository.findByToken(token).getUser();
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

    public Users getUserForReset(String email){
        var user = new Users();
        if (this.verifyUser(email)){
            user = this.getUserByEmail(email);
        }
        return user;
    }

    public boolean verifyUser(String email){
        boolean flag = false;
        if(this.userRepository.findByEmail(email).isPresent()){
            var user = this.userRepository.findByEmail(email).get();
            if(user.isEnabled()){
                flag = true;
            }
        }
        return flag;
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
        this.verificationRepository.delete(verificationToken);
        return "valid";
    }

    public String validateResetToken(String token){
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
        return "valid";
    }
    
    
    public boolean updatePassword(String token, String password){
        boolean flag = false;
        var user = this.getUserByToken(token);
        if (user != null){
            user.setPassword(this.passwordEncoder.encode(password));
            this.userRepository.save(user);
            this.verificationRepository.delete(this.verificationRepository.findByToken(token));
            flag = true;
        }
        else{
            flag = false;
            throw new UsernameNotFoundException("Invalid token");
        }
        return flag;
    }
    
    public String resetPasswordInit(String email, String url) throws NotFoundException{
        
        var user = this.getUserForReset(email);
        String resetToken = UUID.randomUUID().toString();
        this.saveVerificationToken(user, resetToken);
        String resetUrl = url+"/register/resetPassword?token="+resetToken;
        // emailService.sendVerificationEmail(email, resetUrl);
        // log.info("This is the error {}");
        return url+"/register/resetPassword?token="+resetToken;
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
