package br.com.raulcaj.transactionmodule.domain;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.raulcaj.transactionmodule.controller.NotAcceptableException;
import br.com.raulcaj.transactionmodule.controller.NotFoundException;
import br.com.raulcaj.transactionmodule.controller.TransactionRepository;

@Service
public class TransactionService {
	@Autowired
	private AccountService accountService;

	@Autowired
	private OperationTypeRepository operationTypeRepository;

	@Autowired
	private TransactionRepository transactionRepository;
	
	@Value("${transaction_module.config.payment_operation_id}")
	private long paymentOperationTypeId;

	public void createTransaction(final TransactionRequest transactionRequest) throws Exception {
		validateTransactionRequest(transactionRequest);
		final OperationType operationType = operationTypeRepository.findOne(transactionRequest.getOperationTypeId()).get();
		final Transaction transaction = Transaction.createTx(transactionRequest.getAccountId(), transactionRequest.getAmount(), operationType);
		transactionRepository.save(transaction);		
		accountService.decreaseAccountLimit(transaction.getAccountId(), transaction.getAmountSpent());
	}

	private void validateTransactionRequest(final TransactionRequest transactionRequest)
			throws NotFoundException, NotAcceptableException {
		if(accountService.accountNotExist(transactionRequest.getAccountId())) {
			throw new NotFoundException("Account not found");
		}
		if(transactionRequest.getOperationTypeId() == paymentOperationTypeId) {
			throw new NotAcceptableException("Cannot create payment transaction");
		}
		if(operationTypeRepository.notExists(transactionRequest.getOperationTypeId())) {
			throw new NotFoundException("Operation type not found");
		}
		if(BigDecimal.ZERO.compareTo(transactionRequest.getAmount()) > 0) {
			throw new NotAcceptableException("Amount must be positive");
		}
	}

	public List<Transaction> findAll() {
		return transactionRepository.findAll();
	}
	
	
	
}
