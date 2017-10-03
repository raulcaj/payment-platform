package br.com.raulcaj.transactionmodule.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends Exception {
	private static final long serialVersionUID = 8875039294750423749L;
	
	public NotFoundException() {}
	
	public NotFoundException(final String message) {
		super(message);
	}
}
