package br.com.raulcaj.transactionmodule.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionRequest implements Serializable {
	private static final long serialVersionUID = -7028682729355902365L;
	
	@JsonProperty("account_id")
	private Long accountId;
	
	@JsonProperty("operation_type_id")
	private Long operationTypeId;
	
	private BigDecimal amount;
	
	public static TransactionRequest createTxRequest(final Long account_id, final Long operation_type_id, final BigDecimal amount) {
		TransactionRequest request = new TransactionRequest();
		request.accountId = account_id;
		request.operationTypeId = operation_type_id;
		request.amount = amount;
		return request;
	}

	public Long getAccountId() {
		return accountId;
	}
	
	public Long getOperationTypeId() {
		return operationTypeId;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}
}