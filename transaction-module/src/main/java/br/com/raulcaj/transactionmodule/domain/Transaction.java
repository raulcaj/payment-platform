package br.com.raulcaj.transactionmodule.domain;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.data.util.Pair;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Transaction {
	@Id
	@GeneratedValue
	private Long id;
	@NotNull
	@JsonProperty("account_id")
	private Long accountId;
	@NotNull
	@OneToOne
	@JsonProperty("operation_type")
	private OperationType operationType;
	@NotNull
	private BigDecimal amount;
	@NotNull
	private BigDecimal balance;
	@NotNull
	@JsonProperty("event_date")
	private Long eventDate;
	@NotNull
	@JsonProperty("due_date")
	private Long dueDate;
	
	public Long getId() {
		return id;
	}

	public Long getAccountId() {
		return accountId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public Long getDueDate() {
		return dueDate;
	}

	public Long getEventDate() {
		return eventDate;
	}

	public OperationType getOperationType() {
		return operationType;
	}

	public static Transaction setupTransaction(final Transaction transaction, final Long account_id, final BigDecimal amount,
			final OperationType operationType) {
		transaction.accountId = account_id;
		transaction.operationType = operationType;
		transaction.amount = amount.negate();
		transaction.balance = amount.negate();
		final Date eventDate = new Date();
		transaction.eventDate = eventDate.getTime();
		transaction.dueDate = calculateDueDate(eventDate);
		return transaction;
	}
	
	public static Transaction setupPayment(final Transaction transaction, final Long account_id, final BigDecimal amount, final OperationType operationType) {
		final Transaction payment = setupTransaction(transaction, account_id, amount, operationType);
		payment.amount = amount;
		payment.balance = amount;
		return payment;
	}

	private static Long calculateDueDate(final Date eventDate) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(DateUtils.addDays(eventDate, 40));
		calendar.set(Calendar.DAY_OF_MONTH, 10);
		return calendar.getTime().getTime();
	}

	public Pair<BigDecimal, BigDecimal> updateBalance(final Transaction payment, final Long WITHDRAWAL_OPERATION_ID) {
		final Long previousBalance = this.balance.longValue();
		final Long paymentBalance = payment.balance.longValue();
		final Long newBalance = Math.min(0, previousBalance + paymentBalance);
		final Long amountPaid = Math.abs(Math.abs(previousBalance) - Math.abs(newBalance));
		this.balance = new BigDecimal(newBalance);
		payment.balance = new BigDecimal(paymentBalance - amountPaid);
		return createAmountPair(amountPaid, WITHDRAWAL_OPERATION_ID);
	}

	private Pair<BigDecimal, BigDecimal> createAmountPair(final Long amount, final Long WITHDRAWAL_OPERATION_ID) {
		return Pair.of(this.operationType.getId() == WITHDRAWAL_OPERATION_ID ? BigDecimal.ZERO : new BigDecimal(amount),
				this.operationType.getId() == WITHDRAWAL_OPERATION_ID ? new BigDecimal(amount) : BigDecimal.ZERO);
	}
	
	@JsonIgnore
	public Pair<BigDecimal, BigDecimal> getAmountSpent(final Long WITHDRAWAL_OPERATION_ID) {
		return createAmountPair(this.amount.longValue(), WITHDRAWAL_OPERATION_ID);
	}
}
