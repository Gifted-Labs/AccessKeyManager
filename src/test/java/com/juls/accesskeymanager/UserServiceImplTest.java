package com.juls.accesskeymanager;

import com.juls.accesskeymanager.data.models.AuthenticationRequest;
import com.juls.accesskeymanager.data.models.EmailRequest;
import com.juls.accesskeymanager.data.models.Role;
import com.juls.accesskeymanager.data.models.Users;
import com.juls.accesskeymanager.data.repository.UserRepository;
import com.juls.accesskeymanager.data.repository.VerificationTokenRepository;
import com.juls.accesskeymanager.data.token.VerificationToken;
import com.juls.accesskeymanager.exceptions.BadRequestException;
import com.juls.accesskeymanager.exceptions.InvalidPasswordException;
import com.juls.accesskeymanager.exceptions.UserAlreadyExistsException;
import com.juls.accesskeymanager.services.EmailService;
import com.juls.accesskeymanager.services.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private VerificationTokenRepository verificationRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserServiceImpl userService;

    private Users user;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setEmail("kincaiddarius52@gmail.com");
        user.setPassword("123456");
        user.setRole(Role.USER);
    }

    @Test
    void authenticatedUser_ShouldAuthenticateUser() throws BadRequestException {
        // Arrange
        String username = "obokesefoundation1@gmail.com";
        String password = "123456";
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        // Act
        boolean result = userService.authenticatedUser(username, password);

        // Assert
        assertTrue(result);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertEquals(authentication, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void authenticatedUser_ShouldThrowBadRequestException() {
        // Arrange
        String username = "obokesefoundation1@gmail.com";
        String password = "wrongpassword";
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new RuntimeException("Authentication failed"));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.authenticatedUser(username, password));
        assertEquals("Authentication failed", exception.getMessage());
    }

    @Test
    void getUserByEmail_ShouldReturnUser() {
        // Arrange
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // Act
        Users foundUser = userService.getUserByEmail(user.getEmail());

        // Assert
        assertNotNull(foundUser);
        assertEquals(user.getEmail(), foundUser.getEmail());
    }

    @Test
    void registerUser_ShouldCreateNewUser() {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest(user.getEmail(), "password", "password");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(Users.class))).thenReturn(user);

        // Act
        Users newUser = userService.registerUser(request);

        // Assert
        assertNotNull(newUser);
        assertEquals(user.getEmail(), newUser.getEmail());
        verify(userRepository).save(any(Users.class));
    }

    @Test
    void registerUser_ShouldThrowUserAlreadyExistsException() {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest("obokesefoundation1@gmail.com", "password", "password");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // Act & Assert
        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(request));
        assertEquals("User with email " + user.getEmail() + " already exist", exception.getMessage());
    }

    @Test
    void validateToken_ShouldReturnValidWhenTokenIsValid() {
        // Arrange
        VerificationToken token = new VerificationToken("validToken", user);
        when(verificationRepository.findByToken("validToken")).thenReturn(token);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 1);
        token.setExpirationTime(calendar.getTime());

        // Act
        String result = userService.validateToken("validToken");

        // Assert
        assertEquals("valid", result);
        assertTrue(user.isEnabled());
        verify(userRepository).save(user);
        verify(emailService).sendResetSuccessEmail(user.getEmail());
        verify(verificationRepository).delete(token);
    }

    @Test
    void validateToken_ShouldReturnInvalidWhenTokenIsInvalid() {
        // Arrange
        when(verificationRepository.findByToken("invalidToken")).thenReturn(null);

        // Act
        String result = userService.validateToken("invalidToken");

        // Assert
        assertEquals("Invalid Verification Token", result);
    }

    @Test
    void validateToken_ShouldReturnExpiredWhenTokenIsExpired() {
        // Arrange
        VerificationToken token = new VerificationToken("expiredToken", user);
        when(verificationRepository.findByToken("expiredToken")).thenReturn(token);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -1);
        token.setExpirationTime(calendar.getTime());

        // Act
        String result = userService.validateToken("expiredToken");

        // Assert
        assertEquals("Verification Token Expired", result);
        verify(verificationRepository).delete(token);
    }

    @Test
    void updatePassword_ShouldUpdatePasswordWhenTokenIsValid() throws BadRequestException {
        // Arrange
        VerificationToken token = new VerificationToken("validToken", user);
        when(verificationRepository.findByToken("validToken")).thenReturn(token);
        when(userRepository.save(user)).thenReturn(user);

        // Act
        boolean result = userService.updatePassword("validToken", "newPassword");

        // Assert
        assertTrue(result);
        verify(passwordEncoder).encode("newPassword");
        verify(userRepository).save(user);
        verify(verificationRepository).delete(token);
        verify(emailService).sendResetSuccessEmail(user.getEmail());
    }

    @Test
    void updatePassword_ShouldThrowBadRequestExceptionWhenUserIsNotEnabled() {
        // Arrange
        user.setEnabled(false);
        VerificationToken token = new VerificationToken("validToken", user);
        when(verificationRepository.findByToken("validToken")).thenReturn(token);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.updatePassword("validToken", "newPassword"));
        assertEquals("This account is not verified", exception.getMessage());
    }
}
