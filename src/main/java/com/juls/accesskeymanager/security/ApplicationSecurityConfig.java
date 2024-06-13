package com.juls.accesskeymanager.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.juls.accesskeymanager.services.UsersDetailsService;

import lombok.RequiredArgsConstructor;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class ApplicationSecurityConfig {

    private final UsersDetailsService userAuthDetailsService;

    
    /** 
     * @param http
     * @return SecurityFilterChain
     * @throws Exception
     */
    @SuppressWarnings("removal")
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/public/**").permitAll()
                            .requestMatchers("/public/documentation/**").permitAll()
                .requestMatchers("/api/admin/**","/web/admin/**").hasAuthority("ADMIN")
                .requestMatchers("/api/users/**","/web/users/**").hasAuthority("USER")
                .anyRequest().authenticated()
                )
                .formLogin()
                .loginPage("/public/login")
                .loginProcessingUrl("/public/login")
                .successHandler((request,response,authentication) -> {
                    for (GrantedAuthority authority : authentication.getAuthorities()){
                        if (authority.getAuthority().equals("ADMIN")){
                            response.sendRedirect("/web/admin/dashboard");
                        }
                        else if(authority.getAuthority().equals("USER")){
                            response.sendRedirect("/web/users");

                        }
                    }
                })
                .permitAll().and()
                .logout(logout -> logout.permitAll())
                .authenticationManager(authenticationManager());


        return http.build();
     }



    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setUserDetailsService(this.userAuthDetailsService);
        return new ProviderManager(authProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


}
