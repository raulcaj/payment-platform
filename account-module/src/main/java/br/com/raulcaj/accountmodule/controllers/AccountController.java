package br.com.raulcaj.accountmodule.controllers;


import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import org.springframework.web.bind.annotation.PathVariable;
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
	
	public static class AccountLimitParam {
		private Long availble_credit_limit;
		private Long availble_withdraw_limit;
		
		public AccountLimitParam() { }
	}
	
	@RequestMapping(method=RequestMethod.PATCH, value="/v1/accounts/{id}")
	public List<Limit> getAmountLimits(@PathVariable Long id, AccountLimitParam param) {
		return accountRepository.findOne(id).map(curryFirst(this::updateAccountLimits, param)).orElseGet(ArrayList::new);
	}

	private List<Limit> updateAccountLimits(final AccountLimitParam param, final Account account) {
		final ImmutableMap<LimitType, Long> amountByLimitType = ImmutableMap.of(
				LimitType.CREDIT, param.availble_credit_limit,
				LimitType.WITHDRAW, param.availble_withdraw_limit
				);
		for(Limit limit : account.getLimits()) {
			limit.requestUpdate(amountByLimitType.getOrDefault(limit.getType(), 0L));
		}
		return account.getLimits();
	}
	

}
