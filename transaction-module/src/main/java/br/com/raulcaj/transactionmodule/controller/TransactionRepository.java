package br.com.raulcaj.transactionmodule.controller;

import org.springframework.data.repository.Repository;

import br.com.raulcaj.transactionmodule.domain.Transaction;

public interface TransactionRepository extends Repository<Transaction, Long> {

	void save(Transaction tx);
	
}
