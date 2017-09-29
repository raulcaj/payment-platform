package br.com.raulcaj.transactionmodule.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.raulcaj.transactionmodule.domain.AccountService;
import br.com.raulcaj.transactionmodule.domain.OperationType;
import br.com.raulcaj.transactionmodule.domain.OperationTypeRepository;
import br.com.raulcaj.transactionmodule.domain.PaymentRequest;
import br.com.raulcaj.transactionmodule.domain.Transaction;
import br.com.raulcaj.transactionmodule.domain.TxRequest;

@RestController
public class TxController {

	@Autowired
	private AccountService accountService;

	@Autowired
	private OperationTypeRepository operationTypeRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@RequestMapping(method = RequestMethod.POST, value = "/v1/transactions")
	public ResponseEntity<Void> createTx(@RequestBody TxRequest txRequest) throws Exception {		
		final OperationType operationType = operationTypeRepository.findOne(txRequest.getOperation_type_id())
				.orElseThrow(() -> new Exception("Operation type not supported"));
		if (accountService.decreaseAccountLimit(txRequest, operationType)) {
			final Transaction tx = Transaction.createTx(txRequest.getAccount_id(),
					txRequest.getAmount().scaleByPowerOfTen(2).longValue(), operationType);
			transactionRepository.save(tx);
			return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/v1/transactions")
	public ResponseEntity<List<Transaction>> getAllTx() {
		return new ResponseEntity<List<Transaction>>(transactionRepository.findAll(), HttpStatus.OK);
	}
	
	@Transactional
	@RequestMapping(method = RequestMethod.POST, value = "/v1/payments")
	public ResponseEntity<List<PaymentRequest>> createPayment(@RequestBody List<PaymentRequest> payments) throws Exception {
		final OperationType paymentTransaction = operationTypeRepository.findOne(4L)
				.orElseThrow(() -> new Exception("Operation type not supported"));
		for(final PaymentRequest p : payments) {
			accountService.increaseAccountLimit(p, paymentTransaction);
			final Transaction tx = Transaction.createTx(p.getAccount_id(),
					p.getAmount().scaleByPowerOfTen(2).longValue(), paymentTransaction);
			transactionRepository.save(tx);
			p.accept();
		}
		if(payments.stream().anyMatch(PaymentRequest::getAccepted)) {
			return new ResponseEntity<>(payments.stream().filter(PaymentRequest::getAccepted).collect(Collectors.toList()), HttpStatus.ACCEPTED);
		}
		TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		return new ResponseEntity<>(Arrays.asList(), HttpStatus.NOT_ACCEPTABLE);
	}

}
