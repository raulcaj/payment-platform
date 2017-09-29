package br.com.raulcaj.accountmodule.controllers;


import static br.com.raulcaj.accountmodule.util.LambdaUtil.curryLast;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.raulcaj.accountmodule.domain.Account;
import br.com.raulcaj.accountmodule.domain.AccountRepository;
import br.com.raulcaj.accountmodule.domain.AccountService;
import br.com.raulcaj.accountmodule.domain.LimitPatchRequest;


@RestController
public class AccountController {

	@Inject
	private AccountService accountService;
	
	@Inject
	private AccountRepository accountRepository;
	
	@RequestMapping(method=RequestMethod.GET, value="/v1/accounts/{id}")
	public ResponseEntity<Account> getAccountsById(@PathVariable Long id) {
		return accountRepository.findOne(id).map(a -> new ResponseEntity<>(a, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/v1/accounts/limits")
	public ResponseEntity<List<Account>> getAccounts() {
		return new ResponseEntity<List<Account>>(accountRepository.findAll(), HttpStatus.OK);
	}
	
	@Transactional
	@RequestMapping(method=RequestMethod.PATCH, value="/v1/accounts/{id}")
	public ResponseEntity<Void> getAmountLimits(@PathVariable Long id, @RequestBody List<LimitPatchRequest> param) {
		accountRepository.findOne(id).ifPresent(curryLast(accountService::updateAccountLimits, param));
		if(param.stream().allMatch(LimitPatchRequest::getAccepted)) {
			return new ResponseEntity<>(HttpStatus.ACCEPTED);
		}
		TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
	}

}
