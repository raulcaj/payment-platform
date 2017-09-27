package br.com.raulcaj.accountmodule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class AccountModuleApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountModuleApplication.class, args);
	}
}
