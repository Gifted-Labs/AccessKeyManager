package com.juls.accesskeymanager.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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


@Configuration
@EnableWebSecurity

@RequiredArgsConstructor
public class ApplicationSecurityConfig {

    private final UsersDetailsService userAuthDetailsService;

    
    /** 
     * @param http The Http Security to be returned
     * @return SecurityFilterChain
     * @throws Exception It throws a Series of Exceptions
     */
    @SuppressWarnings("removal")
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/public/**").permitAll()
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
     public WebMvcConfigurer corsConfigurer(){
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/public/**")
                        .allowedMethods("GET", "POST","DELETE","PUT","REQUEST");
            }
        };
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
