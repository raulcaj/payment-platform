package br.com.raulcaj.transactionmodule.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class PaymentRequest implements Serializable {
	private static final long serialVersionUID = -3192594407782587227L;
	private Long account_id;
	private BigDecimal amount;
	private Boolean accepted = false;
	
	public Long getAccount_id() {
		return account_id;
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
