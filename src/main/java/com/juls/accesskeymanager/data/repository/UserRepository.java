package com.juls.accesskeymanager.data.repository;


import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.juls.accesskeymanager.data.models.Role;
import com.juls.accesskeymanager.data.models.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    
    Optional <Users> findByEmail(String email);
    List <Users> findByRole(Role role);
    


}