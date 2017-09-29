package br.com.raulcaj.accountmodule.domain;

import java.io.Serializable;
import java.util.ArrayList;
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
import javax.validation.constraints.NotNull;

@Entity
public class Account implements Serializable {
	
	private static final long serialVersionUID = 3617484173095871631L;

	@Id
	@GeneratedValue
	private Long id;
	
	@OneToMany
	@MapKey(name="type")
	@NotNull
	private Map<LimitType, Limit> limitByType;
	
	public Account() {
		this.limitByType = Stream.of(new Limit(LimitType.CREDIT), new Limit(LimitType.WITHDRAWAL)).collect(Collectors.toMap(Limit::getType, Function.identity()));
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
	

	
}
