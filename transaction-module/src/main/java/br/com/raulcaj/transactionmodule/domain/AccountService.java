package br.com.raulcaj.transactionmodule.domain;

import java.math.BigDecimal;

import org.springframework.data.util.Pair;

import br.com.raulcaj.transactionmodule.controller.NotAcceptableException;

public interface AccountService {

	void executeAccountLimitUpdate(Long accountId, Pair<BigDecimal, BigDecimal> amount)
			throws NotAcceptableException;

	boolean accountNotExist(Long accountId) throws NotAcceptableException;

}