package com.juls.accesskeymanager;

import com.juls.accesskeymanager.data.models.AccessKeyDetails;
import com.juls.accesskeymanager.data.models.AccessKeys;
import com.juls.accesskeymanager.data.models.Status;
import com.juls.accesskeymanager.data.models.Users;
import com.juls.accesskeymanager.data.repository.AccessKeyRepo;
import com.juls.accesskeymanager.exceptions.BadRequestException;
import com.juls.accesskeymanager.services.AccessKeyService;
import com.juls.accesskeymanager.services.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccessKeyServiceTest {

    @Mock
    private AccessKeyRepo accessKeyRepo;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private AccessKeyService accessKeyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllAccessKeys() {
        // Arrange
        Users user1 = new Users();
        user1.setEmail("obokesefoundation1@gmail.com");
        Users user2 = new Users();
        user2.setEmail("kincaiddarius52@gmail.com");

        AccessKeys key1 = new AccessKeys();
        key1.setKeyId(1L);
        key1.setKeyValue("key1");
        key1.setProcuredDate( new Date(System.currentTimeMillis()));
        key1.setExpiryDate(new Date(System.currentTimeMillis() + 86400000L));
        key1.setStatus(Status.ACTIVE);
        key1.setUser(user1);


        AccessKeys key2 = new AccessKeys();
        key2.setKeyId(2L);
        key2.setKeyValue("key2");
        key2.setProcuredDate( new Date(System.currentTimeMillis()));
        key2.setExpiryDate(new Date(System.currentTimeMillis() + 86400000L));
        key2.setStatus(Status.ACTIVE);
        key2.setUser(user2);



        when(accessKeyRepo.findAll()).thenReturn(Arrays.asList(key1, key2));

        // Act
        List<AccessKeyDetails> result = accessKeyService.getAllAccessKeys();

        // Assert
        assertEquals(2, result.size());
        assertEquals("key1", result.get(0).getKeyValue());
        assertEquals("obokesefoundation1@gmail.com", result.get(0).getEmail());
        assertEquals("key2", result.get(1).getKeyValue());
        assertEquals("kincaiddarius52@gmail.com", result.get(1).getEmail());
    }

    @Test
    void testGetActiveKeyByEmail() {
        // Arrange
        Users user = new Users();
        user.setEmail("obokesefoundation1@gmail.com");
        AccessKeys activeKey = new AccessKeys();
        activeKey.setKeyId(1L);
        activeKey.setKeyValue("active_key");
        activeKey.setProcuredDate( new Date(System.currentTimeMillis()));
        activeKey.setExpiryDate(new Date(System.currentTimeMillis() + 86400000L));
        activeKey.setStatus(Status.ACTIVE);
        activeKey.setUser(user);

        AccessKeys revokedKey = new AccessKeys();
        AccessKeys key2 = new AccessKeys();
        revokedKey.setKeyId(2L);
        revokedKey.setKeyValue("revoked_Key");
        revokedKey.setProcuredDate( new Date(System.currentTimeMillis()));
        revokedKey.setExpiryDate(new Date(System.currentTimeMillis() + 86400000L));
        revokedKey.setStatus(Status.REVOKED);
        revokedKey.setUser(user);


        when(accessKeyRepo.findByUser(user)).thenReturn(Arrays.asList(activeKey, revokedKey));
        when(userService.getUserByEmail("obokesefoundation1@gmail.com")).thenReturn(user);

        // Act
        AccessKeyDetails result = accessKeyService.getActiveKeyByEmail("obokesefoundation1@gmail.com");

        // Assert
        assertNotNull(result);
        assertEquals("active_key", result.getKeyValue());
        assertEquals(Status.ACTIVE, result.getStatus());
    }

    @Test
    void testRevokeKey() throws BadRequestException {
        // Arrange
        Users user = new Users();
        user.setEmail("obokesefoundation1@gmail.com");
        AccessKeys activeKey = new AccessKeys();
        activeKey.setKeyId(1L);
        activeKey.setKeyValue("active_key");
        activeKey.setProcuredDate(new Date(System.currentTimeMillis()));
        activeKey.setExpiryDate(new Date(System.currentTimeMillis() + 86400000L));
        activeKey.setStatus(Status.ACTIVE);
        activeKey.setUser(user);
        accessKeyRepo.save(activeKey);

        when(accessKeyService.getActiveKeyByStatus(user.getEmail())).thenReturn(Optional.of(activeKey));
        when(userService.getUserByEmail("obokesefoundation1@gmail.com")).thenReturn(user);

        // Act
        AccessKeys revokedKey = accessKeyService.revokeKey(user.getEmail());

        // Assert
        assertEquals(Status.REVOKED, revokedKey.getStatus());
        verify(accessKeyRepo, times(1)).save(activeKey);
    }

    @Test
    void testGenerateKey() throws BadRequestException {
        // Arrange
        Users user = new Users();
        user.setEmail("obokesefoundation1@gmail.com");
        when(userService.getUserByEmail("obokesefoundation1@gmail.com")).thenReturn(user);
        when(accessKeyService.getActiveKeyByEmail("obokesefoundation1@gmail.com")).thenReturn(null);

        // Act
        AccessKeys generatedKey = accessKeyService.generateKey("obokesefoundation1@gmail.com");

        // Assert
        assertNotNull(generatedKey);
        assertEquals(Status.ACTIVE, generatedKey.getStatus());
        assertEquals(32, generatedKey.getKeyValue().length());
        verify(accessKeyRepo, times(1)).save(any(AccessKeys.class));
    }
}