package br.com.raulcaj.accountmodule.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

import com.google.common.base.Functions;

@Entity
public class Account implements Serializable {
	
	private static final long serialVersionUID = 3617484173095871631L;

	@Id
	@GeneratedValue
	private Long id;
	
	@OneToMany
	@MapKey(name="type")
	private Map<LimitType, Limit> limitByType;
	
	public Account() {
		this.limitByType = Stream.of(new Limit(LimitType.CREDIT), new Limit(LimitType.WITHDRAW)).collect(Collectors.toMap(Limit::getType, Function.identity()));
	}
	
	public Long getId() {
		return id;
	}
	
	public Optional<Limit> limitByType(LimitType type) {
		return Optional.ofNullable(limitByType.getOrDefault(type, null));
	}
	
	public List<Limit> getLimits() {
		return new ArrayList<>(this.limitByType.values());
	}
	
	public enum LimitType implements Serializable {
		CREDIT("credit"), WITHDRAW("withdraw");
		private LimitType(String id) {
			this.id = id;
		}
		private final String id;
		
		public String getId() {
			return id;
		}
		
		public Optional<LimitType> getById(String id) {
			return Arrays.stream(values()).filter(Functions.compose(id::equals, LimitType::getId)::apply).findAny();
		}
	}
	
	@Entity
	public static class Limit implements Serializable {
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
		
		public long requestUpdate(final long ammount) {
			return ammount < 0 ? decrease(-ammount) : increase(ammount);
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
	
}
