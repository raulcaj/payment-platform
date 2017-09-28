package br.com.raulcaj.accountmodule.controllers;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableMap;

import br.com.raulcaj.accountmodule.domain.AccountRepository;
import br.com.raulcaj.accountmodule.domain.Account;
import br.com.raulcaj.accountmodule.domain.Account.Limit;
import br.com.raulcaj.accountmodule.domain.Account.LimitType;

import static br.com.raulcaj.accountmodule.util.LambdaUtil.curryFirst;


@RestController
public class AccountController {

	@Inject
	private AccountRepository accountRepository;
	
	public static class AccountLimitParam implements Serializable {
		private static final long serialVersionUID = -5150572878042492966L;
		private available_limit available_credit_limit;
		private available_limit available_withdrawal_limit;
		public static class available_limit implements Serializable {
			private static final long serialVersionUID = -9018657096739262225L;
			private BigDecimal amount;
			public BigDecimal getAmount() {
				return amount;
			}
			public available_limit() {}
		}
		public available_limit getAvailable_credit_limit() {
			return available_credit_limit;
		}
		public available_limit getAvailable_withdrawal_limit() {
			return available_withdrawal_limit;
		}
		public AccountLimitParam() { }
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/v1/accounts/limits")
	public List<Account> getAccounts() {
		return accountRepository.findAll();
	}
	
	@RequestMapping(method=RequestMethod.PATCH, value="/v1/accounts/{id}")
	public List<Limit> getAmountLimits(@PathVariable Long id, @RequestBody AccountLimitParam param) {
		return accountRepository.findOne(id).map(curryFirst(this::updateAccountLimits, param)).orElseGet(ArrayList::new);
	}

	private List<Limit> updateAccountLimits(final AccountLimitParam param, final Account account) {
		final ImmutableMap<LimitType, Long> amountByLimitType = ImmutableMap.of(
				LimitType.CREDIT, param.available_credit_limit.amount.scaleByPowerOfTen(2).longValue(),
				LimitType.WITHDRAW, param.available_withdrawal_limit.amount.scaleByPowerOfTen(2).longValue()
				);
		for(Limit limit : account.getLimits()) {
			limit.requestUpdate(amountByLimitType.getOrDefault(limit.getType(), 0L));
		}
		accountRepository.save(account);
		return account.getLimits();
	}
	

}