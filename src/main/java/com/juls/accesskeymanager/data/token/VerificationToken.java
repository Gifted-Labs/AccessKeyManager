package com.juls.accesskeymanager.data.token;



import java.util.Calendar;
import java.util.Date;

import com.juls.accesskeymanager.data.models.Users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class VerificationToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;

    @Column(nullable = false)
    private String token;

    private Date expirationTime;

    @OneToOne
    @JoinColumn(name="user_id")
    private Users user;

    public VerificationToken(String token, Users user){
        this.token = token;
        this.user = user;

        this.expirationTime = this.getTokenExpirationTime();
    }

    private static final int EXPIRATIONTIME = 20;

     // A method that handles the calculation of the expiration time.
     protected Date getTokenExpirationTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE,EXPIRATIONTIME);
        return new Date(calendar.getTime().getTime());

    }
}

