package com.juls.accesskeymanager.services;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.juls.accesskeymanager.data.models.AuthenticationRequest;
import com.juls.accesskeymanager.data.models.EmailRequest;
import com.juls.accesskeymanager.data.models.Role;
import com.juls.accesskeymanager.data.models.Users;
import com.juls.accesskeymanager.data.repository.UserRepository;
import com.juls.accesskeymanager.data.repository.VerificationTokenRepository;
import com.juls.accesskeymanager.data.token.VerificationToken;
import com.juls.accesskeymanager.exceptions.BadRequestException;
import com.juls.accesskeymanager.exceptions.InvalidPasswordException;
import com.juls.accesskeymanager.exceptions.NotFoundException;
import com.juls.accesskeymanager.exceptions.UserAlreadyExistsException;


import lombok.RequiredArgsConstructor;


/**
 * @author Julius Adjetey Sowah
 * @version 1.0
 * @since 2024
 */

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    
    @Autowired
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationRepository;
    private final EmailService emailService;

    

    
    /** The getUserByEmail method is used to fetch a user entity from the database.
     * @param email The email of the user you want to search.
     * @return Users    The user if found in the database.
     */
    @Override
    public Users getUserByEmail(String email){
        return this.userRepository.findByEmail(email).get();
    }

    /** 
     * The getUsers method returns a list of all the users who have signed up for the application.
     * @return A list of the user entity.
     */
    public List <Users> getUsers() {
        return this.userRepository.findAll();
    }

    /**
     * The getUserByToken method is used to find users associated to a specific token provided.
     * @param token The auto generated token that is assigned to a user once he request for a password reset or after he registers
     * @return It returns the the details of the particular user associated to the token.
     */
    public Users getUserByToken(String token){
        return verificationRepository.findByToken(token).getUser();
    }



    /**
     * The register user method first checks whether a user is present and throws UserAlreadyExistException if the user exists
     * else it creates an account for the user.
     * @param authenticationRequest It requires an authentication request which contains the details of the user who wants to create the account
     * @return It returns the user entity after the account has been created successfully.
     */
    public Users registerUser(AuthenticationRequest authenticationRequest) {
        Optional <Users> user = this.userRepository.findByEmail(authenticationRequest.email());
        if (user.isPresent()){
            throw new UserAlreadyExistsException("User with email "+authenticationRequest.email()+" already exist");
        }
        else if(!authenticationRequest.password().equals(authenticationRequest.confirm())){
            throw new InvalidPasswordException("The passwords does not match");
        }
        var newUser = new Users();
        newUser.setEmail(authenticationRequest.email());
        newUser.setPassword(passwordEncoder.encode(authenticationRequest.password()));
        newUser.setRole(Role.USER);
        return this.userRepository.save(newUser);
    }

    /**
     * The checkUser method is a method that checks whether a user is exist in the users table and also whether the user's account is not already verified. The method is invoked when the user request the verification email to be resent to them.
     * @param email The email of the user who wants the verification token resent to them.
     * @return It returns the user entity if and only if the user exist.
     */
    public Users checkUser(String email){
        Optional <Users> user = this.userRepository.findByEmail(email);
        var user1 = new Users();
        if (user.isPresent() && !user.get().isEnabled()){
            user1 = user.get();
        }
        return user1;
    }

    /**
     * The getUserForReset() method is used to retrieve the user entity from the user's table so that a reset password verification token can be sent to them.
     * @param email The email of the user who wants to initiate the reset password function
     * @return The user entity if found.
     */
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
                if (verificationRepository.findByUser(user)!=null){
                    
                }
                flag = true;
            }
        }
        return flag;
    }    

    public String getRole(String email){
        return this.getUserByEmail(email).getRole().toString();
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
        
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setReciepient(this.getUserByToken(token).getEmail());  
        this.emailService.sendResetSuccessEmail(emailRequest.getReciepient());
        this.verificationRepository.delete(verificationToken);
        return "valid";
    }

    public String validateResetToken(String token){
        VerificationToken verificationToken = this.verificationRepository.findByToken(token);
        if (verificationToken==null){
            return "Invalid Verification Token";
        }

        Calendar calendar = Calendar.getInstance();
        if((verificationToken.getExpirationTime().getTime() - calendar.getTime().getTime()<= 0)){
            this.verificationRepository.delete(verificationToken);
            return "Verification Token Expired";
        }

        return "valid";
    }
    

    public boolean updatePassword(String token, String password) throws BadRequestException{
        boolean flag = false;
        var user = this.getUserByToken(token);
        
        if (!user.isEnabled()){
            throw new BadRequestException("This account is not verified");
        }
        else {
            user.setPassword(this.passwordEncoder.encode(password));
            this.userRepository.save(user);
            this.verificationRepository.delete(this.verificationRepository.findByToken(token));
            flag = true;
            EmailRequest request = new EmailRequest();
            request.setReciepient(user.getEmail());
            this.emailService.sendResetSuccessEmail(request.getReciepient());
                
        }
        return flag;
    }
    
    public String resetPasswordInit(String email, String url) throws NotFoundException{
        
        var user = this.getUserForReset(email);
        if (user==null){
            throw new UsernameNotFoundException("The user with email {} not found");
        }
        String resetToken = UUID.randomUUID().toString();
        this.saveVerificationToken(user, resetToken);
        String resetLink = url+"/register/resetPassword?token="+resetToken;
        EmailRequest emailRequest = new EmailRequest(email, resetLink);
        this.emailService.sendResetTokenEmail(emailRequest);
        return resetLink;
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
