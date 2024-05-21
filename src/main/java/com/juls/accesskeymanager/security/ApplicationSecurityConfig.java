package com.juls.accesskeymanager.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.juls.accesskeymanager.data.authentication.UserAuthenticationDetails;
import com.juls.accesskeymanager.data.models.Users;
import com.juls.accesskeymanager.services.UserServiceImpl;
import com.juls.accesskeymanager.services.UsersDetailsService;

import lombok.RequiredArgsConstructor;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class ApplicationSecurityConfig {

    private final UserServiceImpl userService;
    private UsersDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/register/**").permitAll()
                .requestMatchers("/admin/").hasAuthority("ADMIN")
                .requestMatchers("/users/**").hasAuthority("USER")
                .anyRequest().authenticated()
                )
                .formLogin()
                .permitAll().and()
                .logout(logout -> logout.permitAll())
                .authenticationManager(authenticationManager());


        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setUserDetailsService(this.userDetailsService);
        return new ProviderManager(authProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return email -> {
            Users user =  this.userService.getUserByEmail(email);
            if (user == null){
                throw new UsernameNotFoundException("User not found with email "+email);
            }
            return new UserAuthenticationDetails(user);
        };
    }

}
