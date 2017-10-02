package br.com.raulcaj.accountmodule.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Optional;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Account implements Serializable {

	private static final long serialVersionUID = 1297562194082211361L;

	@Id
	@GeneratedValue
	private Long id;
	
	@NotNull
	@Min(0L)
	@JsonProperty("available_credit_limit")
	private BigDecimal availableCreditLimit;
	
	@NotNull
	@Min(0L)
	@JsonProperty("available_withdrawal_limit")
	private BigDecimal availableWithdrawalLimit;
	
	public Account() {
	}
	
	public static Account createRequest(final Long availableCreditLimit, final Long availableWithdrawalLimit) {
		final Account a = new Account();
		a.availableCreditLimit = new BigDecimal(availableCreditLimit);
		a.availableWithdrawalLimit = new BigDecimal(availableWithdrawalLimit);
		return a;
	}
	
	public Long getId() {
		return id;
	}
	
	public BigDecimal getAvailableCreditLimit() {
		return availableCreditLimit;
	}
	
	public BigDecimal getAvailableWithdrawalLimit() {
		return availableWithdrawalLimit;
	}
	
	public void updateLimit(@NotNull final Account a) {
		this.availableCreditLimit = Optional.ofNullable(a.availableCreditLimit).map(this.availableCreditLimit::add).orElseGet(this::getAvailableCreditLimit);
		this.availableWithdrawalLimit = Optional.ofNullable(a.availableWithdrawalLimit).map(this.availableWithdrawalLimit::add).orElseGet(this::getAvailableWithdrawalLimit);
	}
}
