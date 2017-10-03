package br.com.raulcaj.transactionmodule.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.raulcaj.transactionmodule.domain.PaymentService;
import br.com.raulcaj.transactionmodule.domain.Transaction;
import br.com.raulcaj.transactionmodule.domain.TransactionRequest;
import br.com.raulcaj.transactionmodule.domain.TransactionService;

@RestController
public class TransactionController {

	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private PaymentService paymentService;
	
	@RequestMapping(method = RequestMethod.GET, value = "/v1/transactions")
	public ResponseEntity<List<Transaction>> getAllTx() {
		return new ResponseEntity<List<Transaction>>(transactionService.findAll(), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/v1/transactions")
	public ResponseEntity<Void> createTransaction(@RequestBody TransactionRequest transactionRequest) throws Exception {
		transactionService.createTransaction(transactionRequest);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/v1/payments")
	public ResponseEntity<Void> createPayment(@RequestBody List<TransactionRequest> listOfTransactioRequest) throws Exception{
		for(final TransactionRequest transactionRequest : listOfTransactioRequest) {
			paymentService.createPayment(transactionRequest);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
