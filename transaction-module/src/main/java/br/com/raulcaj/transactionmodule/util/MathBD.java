package br.com.raulcaj.transactionmodule.util;

import java.math.BigDecimal;

public class MathBD {
	public static BigDecimal max(final BigDecimal fst, final BigDecimal snd) {
		return new BigDecimal(Math.max(fst.longValue(), snd.longValue()));
	}
}
