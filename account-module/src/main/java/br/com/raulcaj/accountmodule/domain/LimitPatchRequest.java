package br.com.raulcaj.accountmodule.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class LimitPatchRequest implements Serializable {
	private static final long serialVersionUID = -6054017498574285794L;
	private String limit_type;
	private BigDecimal amount;
	private Boolean accepted = false;
	
	public String getLimit_type() {
		return limit_type;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}
	
	public void accept() {
		this.accepted = true;
	}
	
	public Boolean getAccepted() {
		return accepted;
	}
}