package com.juls.accesskeymanager;


import com.juls.accesskeymanager.data.models.*;
import com.juls.accesskeymanager.data.repository.AccessKeyRepo;
import com.juls.accesskeymanager.exceptions.BadRequestException;
import com.juls.accesskeymanager.services.AccessKeyService;
import com.juls.accesskeymanager.services.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AccessKeyServiceTest {

    @Mock
    private AccessKeyRepo accessKeyRepo;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private AccessKeyService accessKeyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        String userEmail = "juliusadjeteysowah@@gmail.com";
        String userName = "Test User";
        Users mockUser1 = new Users(); // Create a mock User object
        mockUser1.setEmail(userEmail);
        mockUser1.setRole(Role.ADMIN);
        when(userService.getUserByEmail(userEmail)).thenReturn(mockUser1);

    }

    @Test
    void testGenerateKey_Success() throws BadRequestException {
        // Mock dependencies
        String userEmail = "test@example.com";
        Users mockUser = new Users();
        mockUser.setEmail(userEmail);
        mockUser.setRole(Role.USER);
        when(userService.getUserByEmail(userEmail)).thenReturn(mockUser);
        when(accessKeyRepo.save(any(AccessKeys.class))).thenAnswer(invocation -> {
            AccessKeys key = invocation.getArgument(0);
            key.setKeyId(1L); // Mocking the ID set by repository
            return key;
        });

        // Call method under test
        AccessKeys generatedKey = accessKeyService.generateKey(userEmail);

        // Assertions
        assertEquals(Status.ACTIVE, generatedKey.getStatus());
        assertNotNull(generatedKey.getKeyValue());
        assertNotNull(generatedKey.getProcuredDate());
        assertNotNull(generatedKey.getExpiryDate());
        assertEquals(userEmail, generatedKey.getUser().getEmail());
    }


    @Test
    void testRevokeKey_Success() throws BadRequestException {
        // Mock dependencies
        String userEmail = "test@example.com";
        AccessKeys activeKey = new AccessKeys();
        activeKey.setStatus(Status.ACTIVE);
        when(accessKeyRepo.findByStatusAndUser(Status.ACTIVE, userService.getUserByEmail(userEmail)))
                .thenReturn(Optional.of(activeKey));

        when(accessKeyRepo.save(any(AccessKeys.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Call method under test
        AccessKeys revokedKey = accessKeyService.revokeKey(userEmail);

        // Assertions
        assertEquals(Status.REVOKED, revokedKey.getStatus());
    }

    @Test
    void testRevokeKey_NoActiveKey() {
        // Mock dependencies
        String userEmail = "test@example.com";
        when(accessKeyRepo.findByStatusAndUser(Status.ACTIVE, userService.getUserByEmail(userEmail)))
                .thenReturn(Optional.empty());

        // Test for BadRequestException when no active key is found
        assertThrows(BadRequestException.class, () -> accessKeyService.revokeKey(userEmail));
    }


    @Test
    void testSortKeys_ExpiryDate() {
        // Mock data
        List<AccessKeys> accessKeys = new ArrayList<>();
        accessKeys.add(createAccessKey("key1", Status.ACTIVE, "user1@example.com"));
        accessKeys.add(createAccessKey("key2", Status.ACTIVE, "user2@example.com"));
        accessKeys.add(createAccessKey("key3", Status.ACTIVE, "user3@example.com"));
        Users mockUser = new Users();
        mockUser.setEmail("user2@example.com");
        mockUser.setRole(Role.USER);
        when(accessKeyRepo.findAll()).thenReturn(accessKeys);
        when(userService.getUserByEmail(any())).thenReturn(mockUser);

        // Call method under test
        List<AccessKeyDetails> sortedKeys = accessKeyService.sortKeys("expirydate");

        // Assertions - Example: Add assertions based on expected sorting criteria
        // You need to customize based on your specific sorting logic
        assertNotNull(sortedKeys);
        // Add more assertions as per your sorting criteria
    }

    private AccessKeys createAccessKey(String keyValue, Status status, String userEmail) {
        Users mockUser = new Users();
        mockUser.setEmail(userEmail);
        mockUser.setRole(Role.USER);
        AccessKeys accessKey = new AccessKeys();
        accessKey.setKeyValue(keyValue);
        accessKey.setStatus(status);
        accessKey.setProcuredDate(Date.valueOf(LocalDate.now()));
        accessKey.setExpiryDate(Date.valueOf(LocalDate.now().plusDays(1)));
        accessKey.setUser(mockUser);
        return accessKey;
    }
}
