package br.com.raulcaj.accountmodule.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

import com.google.common.base.Functions;

public enum LimitType implements Serializable {
	CREDIT("credit"), WITHDRAWAL("withdrawal");
	private LimitType(String id) {
		this.id = id;
	}
	private final String id;
	
	public String getId() {
		return id;
	}
	
	public Optional<LimitType> getById(String id) {
		return Arrays.stream(values()).filter(Functions.compose(id::equals, LimitType::getId)::apply).findAny();
	}
}
