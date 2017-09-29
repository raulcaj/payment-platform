package br.com.raulcaj.accountmodule.controllers;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableMap;

import br.com.raulcaj.accountmodule.domain.AccountRepository;
import br.com.raulcaj.accountmodule.domain.AccountLimit;
import br.com.raulcaj.accountmodule.domain.LimitType;
import br.com.raulcaj.accountmodule.domain.Account;

import static br.com.raulcaj.accountmodule.util.LambdaUtil.curryFirst;


@RestController
public class AccountController {

	@Inject
	private AccountRepository accountRepository;
	
	public static class LimitPatchRequest implements Serializable {
		private static final long serialVersionUID = -6054017498574285794L;
		private String limit_type;
		private BigDecimal amount;
		
		public String getLimit_type() {
			return limit_type;
		}
		
		public BigDecimal getAmount() {
			return amount;
		}
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/v1/accounts/{id}")
	public ResponseEntity<Account> getAccountsById(@PathVariable Long id) {
		return accountRepository.findOne(id).map(a -> new ResponseEntity<>(a, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/v1/accounts/limits")
	public ResponseEntity<List<Account>> getAccounts() {
		return new ResponseEntity<List<Account>>(accountRepository.findAll(), HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.PATCH, value="/v1/accounts/{id}")
	public ResponseEntity<Void> getAmountLimits(@PathVariable Long id, @RequestBody List<LimitPatchRequest> param) {
		accountRepository.findOne(id).ifPresent(curryFirst(this::updateAccountLimits, param));
		return new ResponseEntity<>(HttpStatus.OK);
	}

	private void updateAccountLimits(final List<LimitPatchRequest> param, final Account account) {
		final ImmutableMap<LimitType, Long> amountByLimitType = ImmutableMap.of(
				LimitType.CREDIT, extractParamAmount(LimitType.CREDIT, param),
				LimitType.WITHDRAWAL, extractParamAmount(LimitType.WITHDRAWAL, param)
				);
		for(AccountLimit limit : account.getLimits()) {
			limit.requestUpdate(amountByLimitType.getOrDefault(limit.getType(), 0L));
		}
		accountRepository.save(account);
	}

	private long extractParamAmount(final LimitType type, final List<LimitPatchRequest> param) {
		return param.stream().filter(l -> l.getLimit_type().equals(type.getId())).findFirst().map(LimitPatchRequest::getAmount).orElse(BigDecimal.ZERO).scaleByPowerOfTen(2).longValue();
	}
	

}
