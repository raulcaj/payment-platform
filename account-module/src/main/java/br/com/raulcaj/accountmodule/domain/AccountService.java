package br.com.raulcaj.accountmodule.domain;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;

@Service
public class AccountService {
	
	@Inject
	private AccountRepository accountRepository;

	public void updateAccountLimits(final Account account, final List<LimitPatchRequest> param) {
		final ImmutableMap<LimitType, Optional<LimitPatchRequest>> amountByLimitType = ImmutableMap.of(
				LimitType.CREDIT, extractParamAmount(LimitType.CREDIT, param),
				LimitType.WITHDRAWAL, extractParamAmount(LimitType.WITHDRAWAL, param)
				);
		for(final AccountLimit limit : account.getLimits()) {
			limit.requestUpdate(amountByLimitType.getOrDefault(limit.getType(), Optional.empty()));
		}
		accountRepository.save(account);
	}
	

	private Optional<LimitPatchRequest> extractParamAmount(final LimitType type, final List<LimitPatchRequest> param) {
		return param.stream().filter(l -> l.getLimit_type().equals(type.getId())).findFirst();
	}
	
}
