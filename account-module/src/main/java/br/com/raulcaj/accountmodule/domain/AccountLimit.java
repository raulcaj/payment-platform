package br.com.raulcaj.accountmodule.domain;

import java.io.Serializable;
import java.util.Optional;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
public class AccountLimit implements Serializable {
	private static final long serialVersionUID = 6787887856051044310L;

	@Id
	@GeneratedValue
	private Long id;
	
	private LimitType type;
	
	@Min(value=0L, message="balance must always be positive")
	private long balance;
	
	@ManyToOne
	@JsonIgnore
	private Account account;
	
	public AccountLimit() {
	}
	
	public AccountLimit(LimitType type) {
		this.type = type;
		this.balance = 0L;
	}
	
	public long requestUpdate(final Optional<LimitPatchRequest> request) {
		if(!request.isPresent()) {
			return this.balance;
		}
		final LimitPatchRequest limitPatchRequest = request.get();
		final long amount = limitPatchRequest.getAmount().scaleByPowerOfTen(2).longValue();
		if(amount == 0 || this.balance+amount < 0) {
			return this.balance;
		}
		limitPatchRequest.accept();
		this.balance += amount;
		return this.balance;
	}
	
	public long getBalance() {
		return balance;
	}
	
	public LimitType getType() {
		return type;
	}
}
