package com.juls.accesskeymanager;

import com.juls.accesskeymanager.data.models.AccessKeyDetails;
import com.juls.accesskeymanager.data.models.AccessKeys;
import com.juls.accesskeymanager.data.models.Status;
import com.juls.accesskeymanager.data.models.Users;
import com.juls.accesskeymanager.data.repository.AccessKeyRepo;
import com.juls.accesskeymanager.exceptions.BadRequestException;
import com.juls.accesskeymanager.services.AccessKeyService;
import com.juls.accesskeymanager.services.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccessKeyServiceTest {

    @Mock
    private AccessKeyRepo accessKeyRepo;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private AccessKeyService accessKeyService;

    private Users user;
    private AccessKeys accessKey;
    private AccessKeyDetails accessKeyDetails;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setEmail("nkgentle20@gmail.com");

        accessKey = new AccessKeys();
        accessKey.setKeyId(1L);
        accessKey.setKeyValue("testKeyValue");
        accessKey.setProcuredDate(new Date(System.currentTimeMillis()));
        accessKey.setExpiryDate(new Date(System.currentTimeMillis() + 86400000L));
        accessKey.setStatus(Status.ACTIVE);
        accessKey.setUser(user);

        accessKeyDetails = new AccessKeyDetails();
        accessKeyDetails.setKeyValue("testKeyValue");
        accessKeyDetails.setProcured_date(new Date(System.currentTimeMillis()));
        accessKeyDetails.setExpiry_date(new Date(System.currentTimeMillis() + 86400000L));
        accessKeyDetails.setStatus(Status.ACTIVE);
        accessKeyDetails.setEmail("test@example.com");
    }

    @Test
    void getAllAccessKeys_ShouldReturnListOfAccessKeyDetails() {
        // Arrange
        when(accessKeyRepo.findAll()).thenReturn(List.of(accessKey));

        // Act
        List<AccessKeyDetails> result = accessKeyService.getAllAccessKeys();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(accessKey.getKeyValue(), result.get(0).getKeyValue());
        verify(accessKeyRepo).findAll();
    }

    @Test
    void getAllKeysByEmail_ShouldReturnListOfAccessKeyDetails() {
        // Arrange
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(accessKeyRepo.findByUser(user)).thenReturn(List.of(accessKey));

        // Act
        List<AccessKeyDetails> result = accessKeyService.getAllKeysByEmail(user.getEmail());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(accessKey.getKeyValue(), result.get(0).getKeyValue());
        verify(userService).getUserByEmail(user.getEmail());
        verify(accessKeyRepo).findByUser(user);
    }

    @Test
    void getActiveKeyByEmail_ShouldReturnActiveAccessKeyDetails() {
        // Arrange
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(accessKeyRepo.findByUser(user)).thenReturn(List.of(accessKey));

        // Act
        AccessKeyDetails result = accessKeyService.getActiveKeyByEmail(user.getEmail());

        // Assert
        assertNotNull(result);
        assertEquals(Status.ACTIVE, result.getStatus());
        assertEquals(accessKey.getKeyValue(), result.getKeyValue());
    }

    @Test
    void revokeKey_ShouldRevokeActiveKey() throws BadRequestException {
        // Arrange
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(accessKeyRepo.findByUser(user)).thenReturn(List.of(accessKey));
        when(accessKeyRepo.findByKeyValue(accessKey.getKeyValue())).thenReturn(Optional.of(accessKey));

        // Act
        AccessKeys result = accessKeyService.revokeKey(user.getEmail());

        // Assert
        assertNotNull(result);
        assertEquals(Status.REVOKED, result.getStatus());
        verify(accessKeyRepo).save(accessKey);
    }

    @Test
    void revokeKey_ShouldThrowBadRequestExceptionIfKeyIsExpired() {
        // Arrange
        accessKey.setStatus(Status.EXPIRED);
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(accessKeyRepo.findByUser(user)).thenReturn(List.of(accessKey));
        when(accessKeyRepo.findByKeyValue(accessKey.getKeyValue())).thenReturn(Optional.of(accessKey));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> accessKeyService.revokeKey(user.getEmail()));
        assertEquals("You cannot revoke this key", exception.getMessage());
    }

    @Test
    void generateKey_ShouldGenerateNewKey() throws BadRequestException {
        // Arrange
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(accessKeyRepo.findByUser(user)).thenReturn(List.of());

        // Act
        AccessKeys result = accessKeyService.generateKey(user.getEmail());

        // Assert
        assertNotNull(result);
        assertEquals(Status.ACTIVE, result.getStatus());
        verify(accessKeyRepo).save(any(AccessKeys.class));
    }

    @Test
    void generateKey_ShouldThrowBadRequestExceptionIfActiveKeyExists() {
        // Arrange
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(accessKeyRepo.findByUser(user)).thenReturn(List.of(accessKey));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> accessKeyService.generateKey(user.getEmail()));
        assertEquals("You already have an active key", exception.getMessage());
    }
}
