package br.com.raulcaj.transactionmodule.controller;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import br.com.raulcaj.transactionmodule.domain.Transaction;

public interface TransactionRepository extends Repository<Transaction, Long> {

	void save(Transaction tx);

	List<Transaction> findAll();
	
	@Query("select tx from Transaction tx"
			+ " where"
			+ " tx.balance != 0"
			+ " and tx.account_id = ?1"
			+ " and tx.operationType.limit_type != ?2"
			+ " order by tx.operationType.chargeOrder, tx.eventDate")
	List<Transaction> findUnpaidTransactions(Long account_id, Long payment_id);
}
