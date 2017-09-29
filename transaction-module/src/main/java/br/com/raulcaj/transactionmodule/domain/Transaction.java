package br.com.raulcaj.transactionmodule.domain;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.time.DateUtils;

@Entity
public class Transaction {
	@Id
	@GeneratedValue
	private Long id;
	@NotNull
	private Long account_id;
	@NotNull
	@OneToOne
	private OperationType operationType;
	@NotNull
	private Long amount;
	@NotNull
	private Long balance;
	@NotNull
	private Long eventDate;
	@NotNull
	private Long dueDate;
	
	public Long getId() {
		return id;
	}
	
	public Long getAccount_id() {
		return account_id;
	}
	
	public Long getAmount() {
		return amount;
	}
	
	public Long getBalance() {
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
	
	public static Transaction createTx(final Long account_id, final Long amount, final OperationType operationType) {
		final Transaction tx = new Transaction();
		tx.account_id = account_id;
		tx.operationType = operationType;
		tx.amount = amount;
		tx.balance = amount;
		final Date eventDate = new Date();
		tx.eventDate = eventDate.getTime();
		tx.dueDate = calculateDueDate(eventDate);
		return tx;
	}

	private static Long calculateDueDate(final Date eventDate) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(DateUtils.addDays(eventDate, 40));
		calendar.set(Calendar.DAY_OF_MONTH, 10);
		return calendar.getTime().getTime();
	}
}
