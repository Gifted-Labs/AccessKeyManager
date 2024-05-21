package com.juls.accesskeymanager.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
<<<<<<< HEAD
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
=======
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
>>>>>>> parent of 66409c8 (I implemented the authentication method, specifically the user registration and also modified the user table to include the is_enabled field and also created a table for the verificaiton token et all)
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
<<<<<<< HEAD
=======
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.util.InMemoryResource;
>>>>>>> parent of 66409c8 (I implemented the authentication method, specifically the user registration and also modified the user table to include the is_enabled field and also created a table for the verificaiton token et all)
import org.springframework.security.web.SecurityFilterChain;

import com.juls.accesskeymanager.services.UsersDetailsService;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig {

    @Autowired
    private UsersDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
<<<<<<< HEAD
                .requestMatchers("/register/**").permitAll()
                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                .anyRequest().authenticated()
                )
                .formLogin()
                .permitAll()
                .and()
=======
                .requestMatchers("/admin/").hasAuthority("ADMIN")
                .requestMatchers("/users/").hasAuthority("USER")
                .anyRequest().authenticated()
                )
                .formLogin(form -> form.loginPage("/login")
                .permitAll())
>>>>>>> parent of 66409c8 (I implemented the authentication method, specifically the user registration and also modified the user table to include the is_enabled field and also created a table for the verificaiton token et all)
                .logout(logout -> logout.permitAll())
                .authenticationManager(authenticationManager());


        return http.build();
    }

    @SuppressWarnings({ "deprecation", "static-access" })
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setUserDetailsService(this.userDetailsService);
        return new ProviderManager(authProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        @SuppressWarnings("deprecation")
        PasswordEncoder encoder = NoOpPasswordEncoder.getInstance();
        return encoder;
    }

    @Bean
    public UsersDetailsService userDetailsService(){
        var usd = new InMemoryUserDetailsManager();
        

}
