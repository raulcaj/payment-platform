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


/**
 * Account Controller
 * 
 * @author Raul Almeida <raul.caj@gmail.com>
 */
@RestController
public class AccountController {

	@Autowired
	private AccountService accountService;
	
	
	/**
	 * @param id Account id
	 * @return Account full information
	 */
	@RequestMapping(method=RequestMethod.GET, value="/v1/accounts/{id}")
	public ResponseEntity<Account> getAccountsById(@PathVariable Long id) {
		return accountService.findById(id).map(a -> new ResponseEntity<>(a, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
	}
	
	/**
	 * @return All accounts full information
	 */
	@RequestMapping(method=RequestMethod.GET, value="/v1/accounts/limits")
	public ResponseEntity<List<Account>> getAccounts() {
		return new ResponseEntity<List<Account>>(accountService.findAll(), HttpStatus.OK);
	}
	
	/**
	 * @param id Account id
	 * @param patchRequest Account properties to patch
	 * @return HttpStatus.OK
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.PATCH, value="/v1/accounts/{id}")
	public ResponseEntity<Void> getAmountLimits(@PathVariable Long id, @RequestBody Account patchRequest) throws Exception {
		accountService.updateLimits(id, patchRequest);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

}
