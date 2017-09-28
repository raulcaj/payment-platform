package br.com.raulcaj.transactionmodule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class TransactionModuleApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionModuleApplication.class, args);
	}
}
