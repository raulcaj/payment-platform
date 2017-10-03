package br.com.raulcaj.transactionmodule.domain;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Service;

@Service
public class TransactionFactory {
	
	@Autowired
	private AutowireCapableBeanFactory beanFactory;

	public Transaction createTransaction(Long accountId, BigDecimal amount, OperationType operationType) {
		return Transaction.setupTransaction(beanFactory.createBean(Transaction.class), accountId, amount, operationType);
	}

	public Transaction createPayment(Long accountId, BigDecimal amount, OperationType operationType) {
		return Transaction.setupPayment(beanFactory.createBean(Transaction.class), accountId, amount, operationType);
	}

}
