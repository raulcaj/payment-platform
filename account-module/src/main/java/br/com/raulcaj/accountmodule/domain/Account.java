package br.com.raulcaj.accountmodule.domain;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;


@Entity
public class Account {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@OneToMany
	@MapKey
	private Map<String, Limit> limitByType;
	
	public Account() {
		this.limitByType = Stream.of(new Limit("credit"), new Limit("withdraw")).collect(Collectors.toMap(Limit::getType, Function.identity()));
	}
	
	public Long getId() {
		return id;
	}
	
	public Optional<Limit> limitByType(String type) {
		return Optional.ofNullable(limitByType.getOrDefault(type, null));
	}
	
	@Entity
	public class Limit {
		@Id
		@Size(min=1, max=40, message="type length must be between 1 to 40 characters long")
		private String type;
		
		@Min(value=0L, message="balance must always be positive")
		private long balance;
		
		public Limit() {
		}
		
		public Limit(String type) {
			this.type = type;
			this.balance = 0L;
		}
		
		public long increase(final long amount) {
			this.balance += amount;
			return this.balance;
		}
		
		public long decrease(final long amount) throws LimitExceededException {
			if(this.balance - amount < 0) {
				throw new LimitExceededException(this.balance, amount);
			}
			this.balance -= amount;
			return this.balance;
		}
		
		public long getBalance() {
			return balance;
		}
		
		public String getType() {
			return type;
		}
		
		public class LimitExceededException extends Exception {
			private static final long serialVersionUID = 556028030514673098L;
			final long currentBalance;
			final long decreaseRequested;
			public LimitExceededException(final long currentBalance, final long amount) {
				this.currentBalance = currentBalance;
				this.decreaseRequested = amount;
			}
		}
	}
	
}
