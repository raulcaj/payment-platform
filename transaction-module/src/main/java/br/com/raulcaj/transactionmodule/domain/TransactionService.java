package br.com.raulcaj.transactionmodule.domain;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.raulcaj.transactionmodule.controller.NotAcceptableException;
import br.com.raulcaj.transactionmodule.controller.NotFoundException;

@Service
public class TransactionService {
	@Autowired
	private AccountService accountService;

	@Autowired
	private OperationTypeRepository operationTypeRepository;

	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private TransactionFactory transactionFactory;
	
	@Autowired
	private PaymentService paymentService;
	
	@Value("${transaction_module.config.payment_operation_id}")
	private long PAYMENT_OPERATION_ID;
	
	@Value("${transaction_module.config.withdrawal_operation_id}")
	private Long WITHDRAWAL_OPERATION_ID;

	public void createTransaction(final TransactionRequest transactionRequest) throws Exception {
		validateTransactionRequest(transactionRequest);
		final OperationType operationType = operationTypeRepository.findOne(transactionRequest.getOperationTypeId()).get();
		final Transaction transaction = transactionFactory.createTransaction(transactionRequest.getAccountId(), transactionRequest.getAmount(), operationType);
		updateCreditTransactions(transaction);
		transactionRepository.save(transaction);
		accountService.executeAccountLimitUpdate(transaction.getAccountId(), transaction.getAmountLeft(WITHDRAWAL_OPERATION_ID));
	}

	private void updateCreditTransactions(Transaction transaction) {
		final List<Transaction> creditTransactions = transactionRepository.findCreditTransactions(transaction.getAccountId(), PAYMENT_OPERATION_ID);
		for(final Transaction payment : creditTransactions) {
			paymentService.payTransaction(payment, transaction);
			if(BigDecimal.ZERO.compareTo(transaction.getBalance()) == 0) {
				break;
			}
		}
	}

	private void validateTransactionRequest(final TransactionRequest transactionRequest)
			throws NotFoundException, NotAcceptableException {
		if(accountService.accountNotExist(transactionRequest.getAccountId())) {
			throw new NotFoundException("Account not found");
		}
		if(transactionRequest.getOperationTypeId() == PAYMENT_OPERATION_ID) {
			throw new NotAcceptableException("Cannot create payment transaction");
		}
		if(!operationTypeRepository.exists(transactionRequest.getOperationTypeId())) {
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
