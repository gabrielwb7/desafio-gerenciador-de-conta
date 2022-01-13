package com.desafio.manageraccount;

import com.desafio.manageraccount.entities.Account;
import com.desafio.manageraccount.entities.Client;
import com.desafio.manageraccount.services.AccountService;
import com.desafio.manageraccount.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

import static com.desafio.manageraccount.entities.enums.TypeAccount.LEGALPERSON;

@SpringBootApplication
public class ManageraccountApplication implements CommandLineRunner {

	@Autowired
	private ClientService clientService;

	@Autowired
	private AccountService accountService;

	public static void main(String[] args) {
		SpringApplication.run(ManageraccountApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		Client client = new Client(1L,"Gabriel", "111.222.333-44", "(33) 93333-2222", "teste");
		Account account = new Account(null, "2222", "2111", LEGALPERSON, "2", 2000.00,800.00, 30);

		client.getAccountList().addAll(Arrays.asList(account));

		clientService.insertClient(client);
		accountService.insertAccount(account, client.getId());

	}

}
