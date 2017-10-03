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
	
	@Value("${transaction_module.config.payment_operation_id}")
	private long paymentOperationTypeId;
	
	public void createPayment(TransactionRequest paymentRequest) throws Exception {
		validatePaymentRequest(paymentRequest);
		final Transaction payment = Transaction.createTx(paymentRequest.getAccountId(), paymentRequest.getAmount(), operationTypeRepository.findOne(paymentOperationTypeId).get());
		transactionRepository.save(payment);
		final Pair<BigDecimal, BigDecimal> amountPaid = payPreviousTransactions(payment);
		accountService.increaseAccountLimit(paymentRequest.getAccountId(), amountPaid);
	}

	private Pair<BigDecimal, BigDecimal> payPreviousTransactions(final Transaction payment) {
		final List<Transaction> unpaidTransactions = transactionRepository.findUnpaidTransactions(payment.getAccountId(), payment.getOperationType().getId());
		Pair<BigDecimal, BigDecimal> totalPaid = Pair.of(BigDecimal.ZERO, BigDecimal.ZERO);
		for(final Transaction transaction : unpaidTransactions) {
			final Pair<BigDecimal, BigDecimal> amountPaid = transaction.updateBalance(payment);
			transactionRepository.save(transaction);
			final PaymentsTracking paymentsTracking = PaymentsTracking.createPaymentsTracking(payment, transaction, MathBD.max(amountPaid.getFirst(), amountPaid.getSecond()));
			paymentsTrackingRepository.save(paymentsTracking);
			totalPaid = Pair.of(totalPaid.getFirst().add(amountPaid.getFirst()), totalPaid.getSecond().add(amountPaid.getSecond()));
			if(BigDecimal.ZERO.equals(payment.getBalance())) {
				break;
			}
		}
		return totalPaid;
	}

	private void validatePaymentRequest(final TransactionRequest transactionRequest)
			throws NotFoundException, NotAcceptableException {
		if(accountService.accountNotExist(transactionRequest.getAccountId())) {
			throw new NotFoundException("Account not found");
		}
		if(transactionRequest.getOperationTypeId() != paymentOperationTypeId) {
			throw new NotAcceptableException("Cannot create transactions, only payments");
		}
		if(BigDecimal.ZERO.compareTo(transactionRequest.getAmount()) > 0) {
			throw new NotAcceptableException("Amount must be positive");
		}
	}
	
}
