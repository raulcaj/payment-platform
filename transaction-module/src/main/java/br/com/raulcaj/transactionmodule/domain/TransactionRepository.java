package br.com.raulcaj.transactionmodule.domain;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface TransactionRepository extends Repository<Transaction, Long> {

	void save(Transaction tx);

	List<Transaction> findAll();
	
	@Query("select tx from Transaction tx"
			+ " where"
			+ " tx.balance < 0"
			+ " and tx.accountId = ?1"
			+ " and tx.operationType.id != ?2"
			+ " order by tx.operationType.chargeOrder, tx.eventDate")
	List<Transaction> findUnpaidTransactions(Long account_id, Long payment_id);
	
	@Query("select tx from Transaction tx"
			+ " where"
			+ " tx.balance > 0"
			+ " and tx.accountId = ?1"
			+ " and tx.operationType.id = ?2"
			+ " order by tx.eventDate")	
	List<Transaction> findCreditTransactions(Long account_id, Long payment_id);
}
