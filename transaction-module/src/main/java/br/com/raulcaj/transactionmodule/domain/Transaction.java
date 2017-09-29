package br.com.raulcaj.transactionmodule.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Transaction {
	@Id
	private Long id;
	private Long account_id;
	private Long amount;
	private Long balance;
	private Long eventDate;
	private Long dueDate;
	
}
