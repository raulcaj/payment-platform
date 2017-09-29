package br.com.raulcaj.transactionmodule.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class TxRequest implements Serializable {
	private static final long serialVersionUID = -7028682729355902365L;
	private Long account_id;
	private Long operation_type_id;
	private BigDecimal amount;
	
	public Long getAccount_id() {
		return account_id;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public Long getOperation_type_id() {
		return operation_type_id;
	}
}
