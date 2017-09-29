package br.com.raulcaj.transactionmodule.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Payment {
	@Id
	private Long id;
	@OneToOne
	private Transaction credit;
	@OneToOne
	private Transaction debit;
	private Long amount;

	public Long getId() {
		return id;
	}
	public Long getAmount() {
		return amount;
	}
	public Transaction getCredit() {
		return credit;
	}
	public Transaction getDebit() {
		return debit;
	}
}
