package com.juls.accesskeymanager.data.repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import com.juls.accesskeymanager.data.models.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import com.juls.accesskeymanager.data.models.AccessKeys;

public interface AccessKeyRepo extends JpaRepository<AccessKeys, Long> {
    
    List <AccessKeys> findByUserId(Long usersId);
    Optional <AccessKeys> findByProcuredDate(Date date);
    List <AccessKeys> findByStatus(Status status);
    Optional <AccessKeys> findByKeyValue(String keyValue);

}
