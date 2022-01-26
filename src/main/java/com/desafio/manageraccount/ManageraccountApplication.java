package com.desafio.manageraccount;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class ManageraccountApplication  {
	public static void main(String[] args) {
		SpringApplication.run(ManageraccountApplication.class, args);
	}
}
