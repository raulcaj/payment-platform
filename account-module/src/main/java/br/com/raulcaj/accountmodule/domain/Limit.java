package br.com.raulcaj.accountmodule.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Min;

@Entity
public class Limit implements Serializable {
	private static final long serialVersionUID = 6787887856051044310L;

	@Id
	@GeneratedValue
	private Long id;
	
	private LimitType type;
	
	@Min(value=0L, message="balance must always be positive")
	private long balance;
	
	public Limit() {
	}
	
	public Limit(LimitType type) {
		this.type = type;
		this.balance = 0L;
	}
	
	public long requestUpdate(final long amount) {
		return amount < 0 ? decrease(-amount) : increase(amount);
	}
	
	public long increase(@Min(0L)final long amount) {
		this.balance += amount;
		return this.balance;
	}
	
	public long decrease(@Min(0L)final long amount) {
		if(this.balance - amount < 0) {
			return this.balance;
		}
		this.balance -= amount;
		return this.balance;
	}
	
	public long getBalance() {
		return balance;
	}
	
	public LimitType getType() {
		return type;
	}
}
