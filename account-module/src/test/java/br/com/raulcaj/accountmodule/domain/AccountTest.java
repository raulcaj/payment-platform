package br.com.raulcaj.accountmodule.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;

public class AccountTest {

	@Test
	public void createAnAccount() {
		final Account acc = new Account();
		assertNull(acc.getId());
		assertFalse(acc.limitByType(null).isPresent());
		final Optional<AccountLimit> withdrawalLimit = acc.limitByType(LimitType.WITHDRAWAL);
		final Optional<AccountLimit> opLimit = acc.limitByType(LimitType.CREDIT);
		validationsOnLimits(opLimit);
		validationsOnLimits(withdrawalLimit);
	}

	private void validationsOnLimits(final Optional<AccountLimit> opLimit) {
		assertTrue(opLimit.isPresent());
		final AccountLimit limit = opLimit.get();
		assertEquals(limit.getBalance(), 0L);
	}

}
