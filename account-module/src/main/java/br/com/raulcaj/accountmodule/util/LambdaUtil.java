package br.com.raulcaj.accountmodule.util;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class LambdaUtil {
	public static <A,B,C> Function<B, C> curryFirst(final BiFunction<A, B, C> biFunction, A a) {
		return b -> biFunction.apply(a, b);
	}
	
	public static <A,B> Consumer<B> curryFirst(final BiConsumer<A, B> biConsumer, A a) {
		return b -> biConsumer.accept(a, b);
	}
}
