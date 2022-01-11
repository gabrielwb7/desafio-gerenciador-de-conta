package com.desafio.manageraccount;

import com.desafio.manageraccount.entities.Account;
import com.desafio.manageraccount.entities.Client;
import com.desafio.manageraccount.repositories.AccountRepository;
import com.desafio.manageraccount.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

import static com.desafio.manageraccount.entities.TypeAccount.LEGALPERSON;

@SpringBootApplication
public class ManageraccountApplication implements CommandLineRunner {

	@Autowired
	private ClientService clientService;

	@Autowired
	private AccountRepository accountRepository;

	public static void main(String[] args) {
		SpringApplication.run(ManageraccountApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		Client client = new Client(null,"Gabriel", "126.251.926-82", "(34) 99337-7592", "teste");
		Account account = new Account(null, "2222", "2111", LEGALPERSON, "2", 2000.00,800.00, 30, client);

		client.getAccountList().addAll(Arrays.asList(account));

		clientService.insertClient(client);
		accountRepository.save(account);

	}

}
