package com.juls.accesskeymanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AccesskeymanagerApplication {	

	
	/** 
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(AccesskeymanagerApplication.class, args);
	}

}
