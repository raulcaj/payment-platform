package br.com.raulcaj.transactionmodule.domain;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class PaymentsTracking {
	@Id
	@GeneratedValue
	private Long id;
	
	@OneToOne
	private Transaction credit;
	@OneToOne
	private Transaction debit;
	private BigDecimal amount;
	
	public static PaymentsTracking createPaymentsTracking(final Transaction credit, final Transaction debit, final BigDecimal amount) {
		final PaymentsTracking paymentsTracking = new PaymentsTracking();
		paymentsTracking.credit = credit;
		paymentsTracking.debit = debit;
		paymentsTracking.amount = amount;
		return paymentsTracking;
	}

	public Long getId() {
		return id;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public Transaction getCredit() {
		return credit;
	}
	public Transaction getDebit() {
		return debit;
	}
}
