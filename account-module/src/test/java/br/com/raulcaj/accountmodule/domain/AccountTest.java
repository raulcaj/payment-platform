package br.com.raulcaj.accountmodule.domain;

import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.Test;

import br.com.raulcaj.accountmodule.domain.Account.Limit;
import br.com.raulcaj.accountmodule.domain.Account.LimitType;

public class AccountTest {

	@Test
	public void createAnAccount() {
		final Account acc = new Account();
		assertNull(acc.getId());
		assertFalse(acc.limitByType(null).isPresent());
		final Optional<Limit> withdrawalLimit = acc.limitByType(LimitType.WITHDRAWAL);
		final Optional<Limit> opLimit = acc.limitByType(LimitType.CREDIT);
		validationsOnLimits(opLimit);
		validationsOnLimits(withdrawalLimit);
	}

	private void validationsOnLimits(final Optional<Limit> opLimit) {
		assertTrue(opLimit.isPresent());
		final Limit limit = opLimit.get();
		assertEquals(limit.getBalance(), 0L);
		limit.increase(0);
		assertEquals(limit.getBalance(), 0L);
		limit.increase(1000L);
		assertEquals(limit.getBalance(), 1000L);
		limit.decrease(50L);
		assertEquals(limit.getBalance(), 950L);
		limit.decrease(1000L);
		assertEquals(limit.getBalance(), 950L);
	}

}
