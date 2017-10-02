package br.com.raulcaj.accountmodule.controllers;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.raulcaj.accountmodule.domain.Account;
import br.com.raulcaj.accountmodule.domain.AccountService;


@RestController
public class AccountController {

	@Autowired
	private AccountService accountService;
	
	
	@RequestMapping(method=RequestMethod.GET, value="/v1/accounts/{id}")
	public ResponseEntity<Account> getAccountsById(@PathVariable Long id) {
		return accountService.findById(id).map(a -> new ResponseEntity<>(a, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/v1/accounts/limits")
	public ResponseEntity<List<Account>> getAccounts() {
		return new ResponseEntity<List<Account>>(accountService.findAll(), HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.PATCH, value="/v1/accounts/{id}")
	public ResponseEntity<Void> getAmountLimits(@PathVariable Long id, @RequestBody Account patchRequest) throws Exception {
		accountService.updateLimits(id, patchRequest);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

}
