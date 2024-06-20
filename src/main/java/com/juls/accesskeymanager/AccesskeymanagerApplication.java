package com.juls.accesskeymanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main entry point for the Access Key Manager application.
 * This Spring Boot application manages access keys for users,
 * providing functionalities for authentication, key generation, and user management.
 * It utilizes Spring Security for authentication and authorization,
 * Spring Data JPA for data persistence, and Spring MVC for web endpoints.
 *
 * <p>The application is structured as follows:
 * <ul>
 *     <li>{@link com.juls.accesskeymanager.security.ApplicationSecurityConfig}: Configures security settings,
 *     including authentication, authorization, and login/logout handling.</li>
 *     <li>{@link com.juls.accesskeymanager.services.UsersDetailsService}: Implements Spring Security's {@code UserDetailsService}
 *     to load user-specific data during authentication.</li>
 *     <li>{@link com.juls.accesskeymanager.services.AccessKeyService}: Manages access keys, including generation, retrieval,
 *     and expiration handling.</li>
 *     <li>{@link com.juls.accesskeymanager.web.controllers}: Contains Spring MVC controllers for handling web endpoints,
 *     including user dashboard, key generation, and authentication-related endpoints.</li>
 *     <li>{@link com.juls.accesskeymanager.data.models}: Defines JPA entities such as {@code Users} and {@code AccessKeyDetails}
 *     representing user data and access key information stored in the database.</li>
 *     <li>{@link com.juls.accesskeymanager.exceptions}: Defines custom exceptions like {@code BadRequestException} and {@code NotFoundException}
 *     to handle specific error scenarios within the application.</li>
 * </ul>
 *
 * <p>The application uses Spring's scheduling capabilities ({@link EnableScheduling}) for tasks such as
 * periodically checking and managing access key expirations.
 *
 * <p>To run the application, execute the {@link #main(String[])} method.
 * This initializes the Spring context and starts the embedded web server,
 * making the application accessible via configured HTTP endpoints.
 *
 * <p>Example usage scenarios:
 * <ul>
 *     <li>Users can authenticate via the login page and access their dashboard to view and manage their access keys.</li>
 *     <li>Admin users have additional permissions to manage users, roles, and perform administrative tasks.</li>
 *     <li>Access keys are automatically generated and managed based on user actions, with expiration policies enforced.</li>
 *     <li>Email notifications are sent for password resets, key generation, and expiration alerts.</li>
 * </ul>
 */
@SpringBootApplication
@EnableScheduling
public class AccesskeymanagerApplication {



	/**
	 * Main method to start the Access Key Manager application.
	 * Initializes the Spring Boot application context and starts the embedded web server.
	 *
	 * @param args Command-line arguments (not used in this application).
	 */
	public static void main(String[] args) {
		SpringApplication.run(AccesskeymanagerApplication.class, args);
	} 
}
