package br.com.raulcaj.accountmodule.domain;

import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.Test;

import br.com.raulcaj.accountmodule.domain.Account.Limit;
import br.com.raulcaj.accountmodule.domain.Account.Limit.LimitExceededException;
import br.com.raulcaj.accountmodule.domain.Account.LimitType;

public class AccountTest {

	@Test
	public void createAnAccount() {
		final Account acc = new Account();
		assertNull(acc.getId());
		assertFalse(acc.limitByType(null).isPresent());
		final Optional<Limit> withdrawLimit = acc.limitByType(LimitType.WITHDRAW);
		final Optional<Limit> opLimit = acc.limitByType(LimitType.CREDIT);
		validationsOnLimits(opLimit);
		validationsOnLimits(withdrawLimit);
	}

	private void validationsOnLimits(final Optional<Limit> opLimit) {
		assertTrue(opLimit.isPresent());
		final Limit limit = opLimit.get();
		assertEquals(limit.getBalance(), 0L);
		limit.increase(0);
		assertEquals(limit.getBalance(), 0L);
		limit.increase(1000L);
		assertEquals(limit.getBalance(), 1000L);
		try {
			limit.decrease(50L);
		} catch (LimitExceededException e) {
			fail(e.getMessage());
		}
		assertEquals(limit.getBalance(), 950L);
		try {
			limit.decrease(1000L);
		} catch (LimitExceededException e) {
			return;
		}
		fail("shouldn't let it decrease by 1000");
	}

}
