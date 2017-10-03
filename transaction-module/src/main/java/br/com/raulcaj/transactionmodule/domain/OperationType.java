package br.com.raulcaj.transactionmodule.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Entity
public class OperationType {
	@Id
	@GeneratedValue
	private Long id;
	
	@Size(min=3, max=20)
	private String description;
	
	@Min(0)
	private Long chargeOrder;
	
	public Long getId() {
		return id;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Long getChargeOrder() {
		return chargeOrder;
	}
	
}
