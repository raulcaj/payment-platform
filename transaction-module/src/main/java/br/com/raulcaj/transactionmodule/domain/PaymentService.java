package br.com.raulcaj.transactionmodule.domain;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import br.com.raulcaj.transactionmodule.controller.NotAcceptableException;
import br.com.raulcaj.transactionmodule.controller.NotFoundException;
import br.com.raulcaj.transactionmodule.util.MathBD;

@Service
public class PaymentService {
	
	@Autowired
	private AccountService accountService;

	@Autowired
	private OperationTypeRepository operationTypeRepository;

	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private PaymentsTrackingRepository paymentsTrackingRepository;
	
	@Autowired
	private TransactionFactory transactionFactory;
	
	@Value("${transaction_module.config.payment_operation_id}")
	private long PAYMENT_OPERATION_ID;
	
	@Value("${transaction_module.config.withdrawal_operation_id}")
	private Long WITHDRAWAL_OPERATION_ID;
	
	public void createPayment(TransactionRequest transactionRequest) throws Exception {
		final TransactionRequest paymentRequest = TransactionRequest.createTxRequest(transactionRequest.getAccountId(), PAYMENT_OPERATION_ID, transactionRequest.getAmount());
		validatePaymentRequest(paymentRequest);
		final Transaction payment = transactionFactory.createPayment(paymentRequest.getAccountId(), paymentRequest.getAmount(), operationTypeRepository.findOne(PAYMENT_OPERATION_ID).get());
		transactionRepository.save(payment);
		final Pair<BigDecimal, BigDecimal> amountPaid = payPreviousTransactions(payment);
		accountService.executeAccountLimitUpdate(paymentRequest.getAccountId(), amountPaid);
	}

	private Pair<BigDecimal, BigDecimal> payPreviousTransactions(final Transaction payment) {
		final List<Transaction> unpaidTransactions = transactionRepository.findUnpaidTransactions(payment.getAccountId(), payment.getOperationType().getId());
		Pair<BigDecimal, BigDecimal> totalPaid = Pair.of(BigDecimal.ZERO, BigDecimal.ZERO);
		for(final Transaction transaction : unpaidTransactions) {
			final Pair<BigDecimal, BigDecimal> amountPaid = payTransaction(payment, transaction);
			totalPaid = Pair.of(totalPaid.getFirst().add(amountPaid.getFirst()), totalPaid.getSecond().add(amountPaid.getSecond()));
			if(BigDecimal.ZERO.equals(payment.getBalance())) {
				break;
			}
		}
		return totalPaid;
	}

	public Pair<BigDecimal, BigDecimal> payTransaction(final Transaction payment, final Transaction transaction) {
		final Pair<BigDecimal, BigDecimal> amountPaid = transaction.updateBalance(payment, WITHDRAWAL_OPERATION_ID);
		transactionRepository.save(transaction);
		transactionRepository.save(payment);
		final PaymentsTracking paymentsTracking = PaymentsTracking.createPaymentsTracking(payment, transaction, MathBD.max(amountPaid.getFirst(), amountPaid.getSecond()));
		paymentsTrackingRepository.save(paymentsTracking);
		return amountPaid;
	}

	private void validatePaymentRequest(final TransactionRequest transactionRequest)
			throws NotFoundException, NotAcceptableException {
		if(accountService.accountNotExist(transactionRequest.getAccountId())) {
			throw new NotFoundException("Account not found");
		}
		if(transactionRequest.getOperationTypeId() != PAYMENT_OPERATION_ID) {
			throw new NotAcceptableException("Cannot create transactions, only payments");
		}
		if(BigDecimal.ZERO.compareTo(transactionRequest.getAmount()) > 0) {
			throw new NotAcceptableException("Amount must be positive");
		}
	}
	
}
