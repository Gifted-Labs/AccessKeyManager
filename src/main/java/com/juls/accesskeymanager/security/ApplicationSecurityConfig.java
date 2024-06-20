package com.juls.accesskeymanager.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.juls.accesskeymanager.services.UsersDetailsService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * Configuration class for Spring Security settings.
 * This class sets up security filter chains, authentication managers, and CORS settings.
 * It enables web security and configures HTTP security for different request matchers.
 *
 * @version 1.0
 * @since 2024
 */

@Configuration
@EnableWebSecurity

@RequiredArgsConstructor
public class ApplicationSecurityConfig {

    private final UsersDetailsService userAuthDetailsService;


    /**
     * Configures the HTTP security settings including CSRF protection, request authorization,
     * form login settings, and authentication management.
     *
     * @param http the {@link HttpSecurity} to modify
     * @return a {@link SecurityFilterChain} that defines the security filter chain
     * @throws Exception if an error occurs while configuring HTTP security
     */
    @SuppressWarnings("removal")
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/public/**").permitAll()
                            .requestMatchers("/docs/**").permitAll()
                .requestMatchers("/api/admin/**","/web/admin/**").hasAuthority("ADMIN")
                            .requestMatchers("/api/users/**","/web/users/**").hasAuthority("USER")
                .anyRequest().fullyAuthenticated()
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


    /**
     * Configures CORS settings for the application.
     *
     * @return a {@link WebMvcConfigurer} that sets up CORS mappings
     */
     @Bean
     public WebMvcConfigurer corsConfigurer(){
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/public/**")
                        .allowedMethods("GET", "POST","DELETE","PUT");
            }
        };
     }

    /**
     * Configures the authentication manager with a DAO authentication provider
     * that uses the {@link UsersDetailsService} and a {@link PasswordEncoder}.
     *
     * @return an {@link AuthenticationManager} configured with the DAO authentication provider
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setUserDetailsService(this.userAuthDetailsService);
        return new ProviderManager(authProvider);
    }

    /**
     * Provides a password encoder bean that uses the BCrypt hashing algorithm.
     *
     * @return a {@link BCryptPasswordEncoder} instance
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


}
