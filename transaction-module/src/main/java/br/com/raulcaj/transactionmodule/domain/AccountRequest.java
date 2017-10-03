package br.com.raulcaj.transactionmodule.domain;

import java.math.BigDecimal;

import org.springframework.data.util.Pair;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountRequest {
	@JsonProperty("id")
	private Long id;
	@JsonProperty("available_credit_limit")
	private BigDecimal avaiableCreditLimit;
	@JsonProperty("available_withdrawal_limit")
	private BigDecimal avaiableWithdrawalLimit;

	public AccountRequest() {
	}

	public static AccountRequest createAccountRequest(final Long accountId, final Pair<BigDecimal, BigDecimal> amount) {
		final AccountRequest accountRequest = new AccountRequest();
		accountRequest.id = accountId;
		accountRequest.avaiableCreditLimit = amount.getFirst();
		accountRequest.avaiableWithdrawalLimit = amount.getSecond();
		return accountRequest;
	}

	public Long getId() {
		return id;
	}

	public BigDecimal getAvaibleCreditLimit() {
		return avaiableCreditLimit;
	}

	public BigDecimal getAvaibleWithdrawalLimit() {
		return avaiableWithdrawalLimit;
	}
}